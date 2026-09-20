package sv.edu.utec.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import sv.edu.utec.modelo.Producto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoApi {

    private int id;
    private String title;
    private int stock;

    public ProductoApi() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Producto aProducto() {
        Producto p = new Producto();

        // Si title supera 50 caracteres, se recorta (VARCHAR(50))
        String nombreFinal = this.title;
        if (nombreFinal != null && nombreFinal.length() > 50) {
            nombreFinal = nombreFinal.substring(0, 50);
        }

        p.setId(this.id);
        p.setNombre(nombreFinal);
        p.setCantidad(this.stock);

        return p;
    }
}