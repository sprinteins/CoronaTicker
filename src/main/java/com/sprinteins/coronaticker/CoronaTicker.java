package com.sprinteins.coronaticker;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CoronaTicker {

	private static String TEAMS_WEBHOOK = "https://outlook.office.com/webhook/..........";

	private static OkHttpClient client;
	private static final String API_FIELD_NAME = "county";

	private static List<Standort> standorte = new ArrayList<>();
	private static List<Link> actions = new ArrayList<>();

	static {

		Standort stuttgart = new Standort("Stuttgart");
		List<Landkreis> stuttgartLk = new ArrayList<>();
		stuttgartLk.add(new Landkreis("Stuttgart", "SK Stuttgart", true));
		stuttgartLk.add(new Landkreis("Böblingen", "LK Böblingen"));
		stuttgartLk.add(new Landkreis("Esslingen", "LK Esslingen"));
		stuttgartLk.add(new Landkreis("Rems-Murr-Kreis", "LK Rems-Murr-Kreis"));
		stuttgartLk.add(new Landkreis("Ludwigsburg", "LK Ludwigsburg"));
		stuttgartLk.add(new Landkreis("Zollernalbkreis", "LK Zollernalbkreis"));
		stuttgartLk.add(new Landkreis("Pforzheim", "SK Pforzheim"));
		stuttgart.getRelevantLandkreise().addAll(stuttgartLk);
		standorte.add(stuttgart);

		Standort bonn = new Standort("Bonn");
		List<Landkreis> bonnLk = new ArrayList<>();
		bonnLk.add(new Landkreis("Bonn", "SK Bonn", true));
		bonnLk.add(new Landkreis("Rhein-Sieg-Kreis", "LK Rhein-Sieg-Kreis"));
		bonnLk.add(new Landkreis("Köln", "SK Köln"));
		bonnLk.add(new Landkreis("Ahrweiler", "LK Ahrweiler"));
		bonnLk.add(new Landkreis("Altenkirchen", "LK Altenkirchen"));
		bonnLk.add(new Landkreis("Rheinisch-Bergischer Kreis", "LK Rheinisch-Bergischer Kreis"));
		bonnLk.add(new Landkreis("Düsseldorf", "SK Düsseldorf"));
		bonnLk.add(new Landkreis("Aachen", "StadtRegion Aachen"));
		bonn.getRelevantLandkreise().addAll(bonnLk);
		standorte.add(bonn);

		Standort muenchen = new Standort("München");
		List<Landkreis> muenchenLk = new ArrayList<>();
		muenchenLk.add(new Landkreis("München", "SK München", true));
		muenchen.getRelevantLandkreise().addAll(muenchenLk);
		standorte.add(muenchen);

		actions.add(
				new Link("RKI Dashboard", "https://experience.arcgis.com/experience/478220a4c454480e823b17327b2bf1d4"));
		// Add optional additional links here...
	}

	public static void main(String[] args) throws IOException {
		client = new OkHttpClient().newBuilder().build();

		loadData();

		publishToTeams();

	}

	private static void loadData() throws IOException {
		Request request = new Request.Builder().url(buildDataUrl()).method("GET", null).build();
		Response response = client.newCall(request).execute();

		JsonObject json = new JsonParser().parse(response.body().string()).getAsJsonObject();

		JsonArray jsonArray = json.get("features").getAsJsonArray();

		for (int i = 0; i < jsonArray.size(); i++) {

			JsonObject dataSet = jsonArray.get(i).getAsJsonObject().get("attributes").getAsJsonObject();
			String region = dataSet.get(API_FIELD_NAME).getAsString();
			BigDecimal data = new BigDecimal(dataSet.get("cases7_per_100k").getAsString()).setScale(2,
					BigDecimal.ROUND_HALF_UP);

			storeRegionToVariable(region, data);
		}

	}

	private static String buildDataUrl() {
		int idx = 0;

		StringBuffer sb = new StringBuffer();

		sb.append(
				"https://services7.arcgis.com/mOBPykOjAyBO2ZKk/arcgis/rest/services/RKI_Landkreisdaten/FeatureServer/0/query?where=%20(");
		for (Standort standort : standorte) {
			for (Landkreis landkreis : standort.getRelevantLandkreise()) {
				if (idx != 0) {
					sb.append("%20OR%20");
				}
				sb.append(API_FIELD_NAME + "%20%3D%20%27");
				sb.append(landkreis.getApiName());
				sb.append("%27");

				idx++;
			}
		}

		sb.append(")%20&outFields=" + API_FIELD_NAME + ",cases7_per_100k&returnGeometry=false&orderByFields="
				+ API_FIELD_NAME + "%20ASC&outSR=4326&f=json");

		return sb.toString();
	}

	public static void storeRegionToVariable(String name, BigDecimal value) {
		for (Standort standort : standorte) {
			for (Landkreis landkreis : standort.getRelevantLandkreise()) {
				if (landkreis.getApiName().equals(name)) {
					landkreis.setCases(value);
					return;
				}
			}
		}
	}

	private static void publishToTeams() throws IOException {
		RequestBody body = RequestBody.create(buildJson(), MediaType.parse("application/json"));
		Request request = new Request.Builder().url(TEAMS_WEBHOOK).method("POST", body)
				.addHeader("Content-Type", "application/json").build();
		client.newCall(request).execute();
	}

	private static String buildJson() {

		JsonObject root = new JsonObject();

		root.addProperty("@type", "MessageCard");
		root.addProperty("@context", "https://schema.org/extensions");
		root.addProperty("summary", "Corona-Ticker");
		root.addProperty("themeColor", getOveralRisk().getHexColor());

		JsonArray sections = new JsonArray();

		JsonObject generalSection = new JsonObject();
		generalSection.addProperty("activityTitle", "SprintEins Corona-Ticker");
		generalSection.addProperty("activitySubtitle", "Stand: " + getCurrentTimestamp() + ", 06:00 Uhr");
		generalSection.addProperty("activityImage", "https://i.imgur.com/f5CYc2p.png");
		generalSection.addProperty("text",
				"Die aktuellen Zahlen in den Landkreisen sehen wie folgt aus (Fälle der letzten 7 Tage je 100.000 Einwohner):");
		sections.add(generalSection);

		for (Standort standort : standorte) {

			Risk officeRisk = standort.getOfficeLandkreis().evaluateRisk();

			JsonObject section = new JsonObject();
			section.addProperty("activityTitle",
					standort.getName() + ": " + officeRisk.getIcon() + " " + officeRisk.getMessage());

			JsonArray facts = new JsonArray();

			for (Landkreis landkreis : standort.getRelevantLandkreise()) {
				JsonObject fact = new JsonObject();
				fact.addProperty("name", landkreis.getDisplayName());
				fact.addProperty("value",
						landkreis.getCases().toString() + appendHomeOfficeMessage(officeRisk, landkreis));
				facts.add(fact);
			}

			section.add("facts", facts);
			sections.add(section);
		}

		root.add("sections", sections);

		JsonArray potentialAction = new JsonArray();

		for (Link link : actions) {

			JsonObject action = new JsonObject();
			action.addProperty("@type", "OpenUri");
			action.addProperty("name", link.getName());
			JsonArray targets = new JsonArray();
			JsonObject target = new JsonObject();
			target.addProperty("os", "default");
			target.addProperty("uri", link.getUrl());
			targets.add(target);
			action.add("targets", targets);
			potentialAction.add(action);

		}

		root.add("potentialAction", potentialAction);

		return new Gson().toJson(root);
	}

	private static String appendHomeOfficeMessage(Risk officeRisk, Landkreis landkreis) {

		if (officeRisk == Risk.HIGH) {
			return ""; // Anyways everyone should stay in Home Office, so no message here
		}

		if (landkreis.evaluateRisk() == Risk.HIGH) {
			return " \u26D4\uFE0F-> Home Office!";
		}

		return "";
	}

	private static String getCurrentTimestamp() {
		return new SimpleDateFormat("dd.MM.yyyy").format(new Date());
	}

	private static Risk getOveralRisk() {

		boolean isYellow = false;

		for (Standort standort : standorte) {
			if (standort.getOfficeLandkreis().evaluateRisk() == Risk.HIGH) {
				return Risk.HIGH;
			}

			if (standort.getOfficeLandkreis().evaluateRisk() == Risk.MEDIUM) {
				isYellow = true;
			}
		}

		return isYellow ? Risk.MEDIUM : Risk.LOW;
	}

}
