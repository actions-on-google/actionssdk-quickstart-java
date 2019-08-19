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
import com.google.actions.api.ConstantsKt;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.Confirmation;
import com.google.actions.api.response.helperintent.DateTimePrompt;
import com.google.actions.api.response.helperintent.Permission;
import com.google.actions.api.response.helperintent.Place;
import com.google.api.services.actions_fulfillment.v2.model.DateTime;
import com.google.api.services.actions_fulfillment.v2.model.Location;

public class MyActionsApp extends ActionsSdkApp {

  @ForIntent("actions.intent.MAIN")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String savedColor = (String) request.getUserStorage().get("favoriteColor");
    if (savedColor != null) {
      responseBuilder.add("Hey there! I remember your favorite color is " + savedColor);
      responseBuilder.add("Can you give me another color to remember?");
    } else {
      responseBuilder.add("Hey there! What's your favorite color?");
    }
    return responseBuilder.build();
  }

  // [START asdk_java_guest_check]
  @ForIntent("actions.intent.TEXT")
  public ActionResponse fallback(ActionRequest request) {
    String color = request.getRawInput().getQuery();
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    String verificationStatus = request.getUser().getUserVerificationStatus();
    if (verificationStatus.equals("VERIFIED")) {
      responseBuilder.getUserStorage().put("favoriteColor", color);
      responseBuilder.add("Alright I'll remember that you like " + color + ". See you!");
    } else {
      responseBuilder.add(color + " is my favorite too! I can't " +
        "save that right now but you can tell me again next time!");
    }
    responseBuilder.endConversation();
    return responseBuilder.build();
  }
  // [END asdk_java_guest_check]

}
