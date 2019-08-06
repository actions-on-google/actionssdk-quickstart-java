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
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.actions.api.response.helperintent.RegisterUpdate;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UpdatesApp extends ActionsSdkApp {

  private static final Map<DayOfWeek, Integer> LOWEST_TEMPERATURES = new HashMap<>();
  static {
    LOWEST_TEMPERATURES.put(DayOfWeek.MONDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.TUESDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.WEDNESDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.THURSDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.FRIDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.SATURDAY, 75);
    LOWEST_TEMPERATURES.put(DayOfWeek.SUNDAY, 75);
  }

  private ActionResponse subscribeToDailyUpdates(ActionRequest request) {
    // [START subscribe_to_daily_updates_asdk]
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    return responseBuilder.add(new RegisterUpdate()
        .setIntent("Daily Lowest Temperature")
        .setFrequency("DAILY"))
        .build();
    // [END subscribe_to_daily_updates_asdk]
  }

  private ActionResponse dailyLowestTemperature(ActionRequest request) {
    // [START suggest_daily_updates_asdk]
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    Integer lowestTemperature =
        LOWEST_TEMPERATURES.get(LocalDate.now().getDayOfWeek());
    responseBuilder
        .add("The lowest temperature for today is " +  lowestTemperature + "°F.")
        .add("I can send you daily updates with the lowest temperature of " +
            "the day. Would you like that?")
        .addSuggestions(new String[] {
            "Send daily updates"
        });
    return responseBuilder.build();
    // [END suggest_daily_updates_asdk]
  }

  @ForIntent("actions.intent.MAIN")
  public ActionResponse welcome(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (!request.hasCapability(Capability.SCREEN_OUTPUT.getValue())) {
      // User engagement features aren't currently supported on speaker-only devices
      // See docs: https://developers.google.com/actions/assistant/updates/overview
      responseBuilder.add("Hi! Welcome to Lowest Temperature Updates! " +
          "To learn about user engagement you will need to switch to " +
          "a screened device.");
      responseBuilder.endConversation();
    } else if (!request.getUser().getUserVerificationStatus().equals("VERIFIED")) {
      // User engagement features aren't currently for non-verified users
      // See docs: https://developers.google.com/actions/assistant/guest-users
      responseBuilder.add("Hi! Welcome to Lowest Temperature Updates! " +
          "To learn about user engagement you'll need to be a verified user.");
      responseBuilder.endConversation();
    } else {
      responseBuilder
          .add("Hi! Welcome to Lowest Temperature Updates! " +
              "I can tell you the lowest temperature each day.")
          .addSuggestions(new String[] {
              "Hear lowest temperature"
          });
    }
    return responseBuilder.build();
  }

  @ForIntent("actions.intent.TEXT")
  public ActionResponse handleDialog(ActionRequest request) {
    String rawText = request.getRawInput().getQuery();
    if (rawText.equals("Send daily updates")) {
      return subscribeToDailyUpdates(request);
    } else if (rawText.equals("Hear lowest temperature")) {
      return dailyLowestTemperature(request);
    } else {
      return getResponseBuilder(request)
          .add("I don't know how to handle that " +
          "request. I can send you daily updates with the lowest " +
          "temperature of the day. Would you like that? ")
          .build();
    }
  }

  @ForIntent("Daily Lowest Temperature")
  public ActionResponse lowestTemperature(ActionRequest request) {
    return dailyLowestTemperature(request);
  }

  // [START confirm_daily_updates_subscription_asdk]
  @ForIntent("actions.intent.REGISTER_UPDATE")
  public ActionResponse confirmDailyUpdatesSubscription(ActionRequest request) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    if (request.isUpdateRegistered()) {
      responseBuilder.add("Ok, I'll start giving you daily updates.");
    } else {
      responseBuilder.add("Ok, I won't give you daily updates.");
    }
    return responseBuilder.endConversation().build();
  }
  // [END confirm_daily_updates_subscription_asdk]
}
