/* Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package iam.snippets;

// [START iam_delete_key]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.IamScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class DeleteServiceAccountKey {

  // Deletes a service account key.
  public static void deleteKey(String projectId, String serviceAccountName,
      String serviceAccountKey) {
    // String projectId = "my-project-id";
    // String serviceAccountName = "my-service-account-name";
    // String serviceAccountKey = "key-name";

    Iam service = null;
    try {
      service = initService();
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e);
      return;
    }

    // Construct the service account email.
    // You can modify the ".iam.gserviceaccount.com" to match the service account name in which
    // you want to delete the key.
    // See, https://cloud.google.com/iam/docs/creating-managing-service-account-keys?hl=en#deleting
    String serviceAccountEmail = serviceAccountName + "@" + projectId + ".iam.gserviceaccount.com";
    try {
      String keyToDelete = String.format("projects/-/serviceAccounts/%s/keys/%s",
          serviceAccountEmail, serviceAccountKey);

      // Then you can delete the key
      service.projects().serviceAccounts().keys().delete(keyToDelete).execute();

      System.out.println("Deleted key: " + keyToDelete);
    } catch (IOException e) {
      System.out.println("Unable to delete service account key: \n" + e);
    }
  }

  private static Iam initService() throws GeneralSecurityException, IOException {
    // Use the Application Default Credentials strategy for authentication. For more info, see:
    // https://cloud.google.com/docs/authentication/production#finding_credentials_automatically
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));
    // Initialize the IAM service, which can be used to send requests to the IAM API.
    Iam service =
        new Iam.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
            .setApplicationName("service-account-keys")
            .build();
    return service;
  }
}
// [END iam_delete_key]
