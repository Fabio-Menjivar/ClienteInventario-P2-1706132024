package sv.edu.utec;

import sv.edu.utec.api.ProveedorAPI;
import sv.edu.utec.datos.ProductoDAO;
import sv.edu.utec.modelo.Producto;
import sv.edu.utec.servicio.InventarioJsonService;
import sv.edu.utec.servicio.SincronizacionService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    private static final ProductoDAO dao = new ProductoDAO();
    private static final InventarioJsonService jsonService = new InventarioJsonService();

    private static final String ARCHIVO = "inventario.json";

    public static void main(String[] args) {
        try {
            // 1. Preparar la base de datos e insertar los datos iniciales
            dao.crearTabla();
            System.out.println("Tabla producto lista.");
            sembrarDatos();

            System.out.println("\n--- Inventario inicial ---");
            imprimir(dao.listar());

            // 2. Respaldar en JSON antes de modificar los datos
            jsonService.exportar(ARCHIVO);
            System.out.println("\nRespaldo generado en " + ARCHIVO);

            // 3. Modificar registros en la base de datos
            if (dao.actualizar(new Producto(2, "Monitor 24 pulgadas", 12))) {
                System.out.println("Producto 2 actualizado.");
            }
            if (dao.eliminar(1)) {
                System.out.println("Producto 1 eliminado.");
            }

            System.out.println("\n--- Despues de los cambios ---");
            imprimir(dao.listar());

            // 4. Restaurar desde el respaldo JSON
            int restaurados = jsonService.importar(ARCHIVO);
            System.out.println("\nRegistros restaurados desde JSON: " + restaurados);

            System.out.println("\n--- Inventario final ---");
            imprimir(dao.listar());

            // -------------------------------------------------------------
            // Enunciado 4: Integración de la sincronización con la API
            // -------------------------------------------------------------
            ProveedorAPI proveedorAPI = new ProveedorAPI();
            SincronizacionService sincroService = new SincronizacionService(proveedorAPI, dao);

            // 1. Invocar sincronizar(10) y mostrar el resumen
            int[] resultado = sincroService.sincronizar(10);
            System.out.printf("%nSincronizacion con la API -> insertados: %d | actualizados: %d%n",
                    resultado[0], resultado[1]);

            // 2. Imprimir el inventario resultante sincronizado
            System.out.println("--- Inventario sincronizado ---");
            imprimir(dao.listar());

        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de entrada/salida o API: " + e.getMessage());
        } catch (InterruptedException e) {
            // 3. Manejar la interrupción del hilo (bloque catch no vacío)
            System.out.println("La sincronización fue interrumpida: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // Inserta datos iniciales únicamente si no existen en la BD
    private static void sembrarDatos() throws SQLException {
        if (!dao.existe(1)) dao.insertar(new Producto(1, "Teclado mecanico", 15));
        if (!dao.existe(2)) dao.insertar(new Producto(2, "Monitor 24 pulgadas", 8));
    }

    // Da formato y muestra la lista de productos en la consola
    private static void imprimir(List<Producto> productos) {
        System.out.printf("%-5s %-25s %10s%n", "ID", "PRODUCTO", "CANTIDAD");
        for (Producto p : productos) {
            System.out.printf("%-5d %-25s %10d%n",
                    p.getId(), p.getNombre(), p.getCantidad());
        }
    }
}