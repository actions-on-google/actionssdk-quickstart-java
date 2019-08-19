/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

    import com.google.actions.api.ActionRequest;
    import com.google.actions.api.ActionResponse;
    import com.google.actions.api.ActionsSdkApp;
    import com.google.actions.api.Capability;
    import com.google.actions.api.ConstantsKt;
    import com.google.actions.api.ForIntent;
    import com.google.actions.api.response.ResponseBuilder;
    import com.google.actions.api.response.helperintent.UpdatePermission;
    import com.google.api.services.actions_fulfillment.v2.model.Argument;

public class MyActionsApp extends ActionsSdkApp {

  private ActionResponse subscribeToNotifications(ActionRequest request) {
    // [START subscribe_to_notifications_asdk]
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add(new UpdatePermission().setIntent("Notification"));
    return responseBuilder.build();
    // [END subscribe_to_notifications_asdk]
  }

  @ForIntent("actions.intent.MAIN")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      responseBuilder.add("Hi! Welcome to Push Notifications! To learn " +
          "about push notifications you will need to switch to a screened device.");
      responseBuilder.endConversation();
    } else if (!request.getUser().getUserVerificationStatus().equals("VERIFIED")) {
      responseBuilder.add("Hi! Welcome to Push Notifications! To learn " +
          "about push notifications you'll need to be a verified user.");
      responseBuilder.endConversation();
    } else {
      responseBuilder.add("Hi! Welcome to Push Notifications!");
      // [START suggest_notifications_asdk]
      responseBuilder
          .add("I can send you push notifications. Would you like that?")
          .addSuggestions(new String[] {
              "Send notifications"
          });
      // [END suggest_notifications_asdk]
    }
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.TEXT")
  public ActionResponse handleDialog(ActionRequest request) {
    String rawText = request.getRawInput().getQuery();
    if (rawText.equals("Send notifications")) {
      return subscribeToNotifications(request);
    } else {
      return getResponseBuilder(request)
          .add("I don't know how to handle that request. " +
              "I can send you push notifications. Would you like that? ")
          .build();
    }
  }

  @ForIntent("Notification")
  public ActionResponse notification(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add("You got a push notification!");
    return responseBuilder.build();
  }


  @ForIntent("Subscribe to Notifications")
  public ActionResponse subscribeToNotificationsIntent(ActionRequest request) {
    return subscribeToNotifications(request);
  }

  // [START confirm_notifications_subscription_asdk]
  @ForIntent("actions.intent.PERMISSION")
  public ActionResponse confirmNotificationsSubscription(ActionRequest request) {
    // Verify the user has subscribed for push notifications
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isPermissionGranted()) {
      Argument userId = request.getArgument(ConstantsKt.ARG_UPDATES_USER_ID);
      if (userId != null) {
        // Store the user's ID in the database
      }
      responseBuilder.add("Ok, I'll start alerting you.");
    } else {
      responseBuilder.add("Ok, I won't alert you.");
    }
    responseBuilder.endConversation();
    return responseBuilder.build();
  }
  // [END confirm_notifications_subscription_asdk]
}
