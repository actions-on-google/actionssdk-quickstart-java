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

// [START asdk_java_reprompt]
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
    responseBuilder.add("Hi! Try this sample on a speaker device, and stay silent when the mic is open. If trying on the Actions console simulator, click the no-input button next to the text input field.");
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.TEXT")
  public ActionResponse fallback(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    responseBuilder.add("You said " + request.getRawInput().getQuery());
    responseBuilder.add("Try this sample on a speaker device, and stay silent when the mic is open. If trying on the Actions console simulator, click the no-input button next to the text input field.");
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.NO_INPUT")
  public ActionResponse reprompt(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    int repromptCount = request.getRepromptCount();
    String response;
    if (repromptCount == 0) {
      response = "What was that?";
    } else if (repromptCount == 1) {
      response = "Sorry, I didn't catch that. Could you repeat yourself?";
    } else {
      responseBuilder.endConversation();
      response = "Okay let's try this again later.";
    }
    return responseBuilder.add(response).build();
  }

}
// [END asdk_java_reprompt]
