package com.pukhovkirill.datahub.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.pukhovkirill.datahub.infrastructure.gateway.dto.GatewayCredentials;

public class CredentialsSaver {

    private static final String FILE_PATH = "credentials.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveCredentials(GatewayCredentials credentials) {
        List<GatewayCredentials> credentialsList = loadCredentials();
        credentialsList.add(credentials);

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(credentialsList, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GatewayCredentials> loadCredentials() {
        List<GatewayCredentials> credentialsList;
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<ArrayList<GatewayCredentials>>(){}.getType();
            credentialsList = gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return credentialsList;
    }
}

