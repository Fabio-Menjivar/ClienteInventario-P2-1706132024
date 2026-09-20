package sv.edu.utec.servicio;

import sv.edu.utec.api.ProveedorAPI;
import sv.edu.utec.datos.ProductoDAO;
import sv.edu.utec.modelo.Producto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SincronizacionService {

    private final ProveedorAPI proveedorAPI;
    private final ProductoDAO productoDAO;

    // 1. Recibe por constructor ProveedorAPI y ProductoDAO
    public SincronizacionService(ProveedorAPI proveedorAPI, ProductoDAO productoDAO) {
        this.proveedorAPI = proveedorAPI;
        this.productoDAO = productoDAO;
    }

    /**
     * Sincroniza los productos obtenidos de la API con la base de datos de manera idempotente.
     *
     * @param limite Cantidad de productos a solicitar al proveedor.
     * @return int[] donde la posición [0] es la cantidad de productos insertados
     *         y la posición [1] es la cantidad de productos actualizados.
     * @throws IOException
     * @throws InterruptedException
     */
    public int[] sincronizar(int limite) throws IOException, InterruptedException, SQLException {
        // 2. Obtener productos del proveedor
        List<Producto> productos = proveedorAPI.obtenerProductos(limite);

        int insertados = 0;
        int actualizados = 0;

        for (Producto producto : productos) {
            // Verificar si el producto ya existe mediante su ID en la BD
            if (productoDAO.existe(producto.getId())) {
                productoDAO.actualizar(producto);
                actualizados++;
            } else {
                productoDAO.insertar(producto);
                insertados++;
            }
        }

        // 3. Devolver cuántos se insertaron y cuántos se actualizaron: [insertados, actualizados]
        return new int[]{insertados, actualizados};
    }
}