package sv.edu.utec.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import sv.edu.utec.modelo.Producto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ProveedorAPI {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ProveedorAPI() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Producto> obtenerProductos(int limite) throws IOException, InterruptedException {
        // 1. Construir la URL y enviar solicitud GET
        String url = "https://dummyjson.com/products?limit=" + limite + "&select=title,stock";

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> respuesta = httpClient.send(solicitud, HttpResponse.BodyHandlers.ofString());

        // 2. Validar código de estado (debe ser 200)
        int codigo = respuesta.statusCode();
        if (codigo != 200) {
            throw new IOException("Error al consultar la API del proveedor. Código recibido: " + codigo);
        }

        // 3. Deserializar el cuerpo a RespuestaProductos
        RespuestaProductos respuestaProductos = objectMapper.readValue(respuesta.body(), RespuestaProductos.class);

        // 4. Convertir cada ProductoApi con aProducto() y devolver la lista de Producto
        List<Producto> listaProductos = new ArrayList<>();
        if (respuestaProductos != null && respuestaProductos.getProducts() != null) {
            for (ProductoApi pApi : respuestaProductos.getProducts()) {
                listaProductos.add(pApi.aProducto());
            }
        }

        return listaProductos;
    }
}