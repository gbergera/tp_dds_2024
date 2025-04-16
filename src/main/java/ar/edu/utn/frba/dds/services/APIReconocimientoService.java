package ar.edu.utn.frba.dds.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class APIReconocimientoService {

    private static final String BASE_URL = "https://d8f0f575-cbbd-4ea1-836c-507d57d0ca9f.mock.pstmn.io/reconocimientos";
    private static HttpClient client = HttpClient.newHttpClient();
    private static ObjectMapper objectMapper;

    public APIReconocimientoService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public APIReconocimientoService(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public static List<ColaboradorDTO> obtenerReconocimientos(double puntos, int donaciones, int maximo) throws IOException, InterruptedException {
        String urlConParametros = String.format("%s?puntos=%.2f&donaciones=%d&maximo=%d", BASE_URL, puntos, donaciones, maximo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlConParametros))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonResponse = response.body();

            // Deserializar la respuesta JSON en una lista de ColaboradorDTO
            List<ColaboradorDTO> colaboradores = objectMapper.readValue(
                    objectMapper.readTree(jsonResponse).get("colaboradores").toString(),
                    new TypeReference<List<ColaboradorDTO>>() {}
            );

            return colaboradores;
        } else {
            throw new RuntimeException("Error en la solicitud: Código de estado " + response.statusCode());
        }
    }


}
