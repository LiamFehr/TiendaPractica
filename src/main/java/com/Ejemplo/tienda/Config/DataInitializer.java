package com.Ejemplo.tienda.Config;

import com.Ejemplo.tienda.Models.Role;
import com.Ejemplo.tienda.Models.Usuario;
import com.Ejemplo.tienda.Models.Producto;
import com.Ejemplo.tienda.Models.Categoria;
import com.Ejemplo.tienda.Repository.UsuarioRepository;
import com.Ejemplo.tienda.Repository.ProductoRepository;
import com.Ejemplo.tienda.Repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UsuarioRepository usuarioRepository, 
                          PasswordEncoder passwordEncoder,
                          ProductoRepository productoRepository,
                          CategoriaRepository categoriaRepository,
                          JdbcTemplate jdbcTemplate) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar y crear columna codigo_interno si no existe
        verificarYCrearColumnaCodigoInterno();
        
        // Crear admin por defecto si no existe ninguno
        if (usuarioRepository.findByEmail("admin@tienda.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador Principal");
            admin.setEmail("admin@tienda.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ROLE_ADMIN);
            usuarioRepository.save(admin);
            System.out.println("✅ Admin por defecto creado: admin@tienda.com / Admin123!");
        }

        // Crear categorías de ejemplo si no existen
        if (categoriaRepository.count() == 0) {
            crearCategoriasEjemplo();
            System.out.println("✅ Categorías de ejemplo creadas");
        }

        // Crear productos de ejemplo si no existen
        if (productoRepository.count() == 0) {
            crearProductosEjemplo();
            System.out.println("✅ Productos de ejemplo creados");
        } else {
            // Solo actualizar productos existentes si ya hay productos
            actualizarProductosSinCodigoInterno();
        }
    }

    private void verificarYCrearColumnaCodigoInterno() {
        try {
            // Verificar si la columna existe
            String sql = """
                SELECT column_name 
                FROM information_schema.columns 
                WHERE table_name = 'producto' 
                AND column_name = 'codigo_interno'
                """;
            
            List<String> columnas = jdbcTemplate.queryForList(sql, String.class);
            
            if (columnas.isEmpty()) {
                System.out.println("🔄 Creando columna codigo_interno en tabla producto...");
                
                // Crear la columna
                jdbcTemplate.execute("ALTER TABLE producto ADD COLUMN codigo_interno VARCHAR(255)");
                
                // Actualizar productos existentes con códigos temporales
                jdbcTemplate.execute("UPDATE producto SET codigo_interno = CONCAT('TEMP-', id) WHERE codigo_interno IS NULL");
                
                // Hacer la columna NOT NULL después de llenarla
                jdbcTemplate.execute("ALTER TABLE producto ALTER COLUMN codigo_interno SET NOT NULL");
                
                // Agregar constraint único
                jdbcTemplate.execute("ALTER TABLE producto ADD CONSTRAINT uk_codigo_interno UNIQUE (codigo_interno)");
                
                System.out.println("✅ Columna codigo_interno creada exitosamente");
            } else {
                System.out.println("ℹ️ Columna codigo_interno ya existe");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al verificar/crear columna codigo_interno: " + e.getMessage());
        }
    }

    private void crearCategoriasEjemplo() {
        // Categoría Tecnología
        Categoria tecnologia = new Categoria();
        tecnologia.setNombre("Tecnología");
        tecnologia.setDescripcion("Productos tecnológicos y electrónicos");
        tecnologia.setColor("#007bff");
        tecnologia.setOrden(1);
        categoriaRepository.save(tecnologia);

        // Categoría Ropa
        Categoria ropa = new Categoria();
        ropa.setNombre("Ropa");
        ropa.setDescripcion("Vestimenta y accesorios");
        ropa.setColor("#28a745");
        ropa.setOrden(2);
        categoriaRepository.save(ropa);

        // Categoría Hogar
        Categoria hogar = new Categoria();
        hogar.setNombre("Hogar");
        hogar.setDescripcion("Artículos para el hogar");
        hogar.setColor("#ffc107");
        hogar.setOrden(3);
        categoriaRepository.save(hogar);
    }

    private void crearProductosEjemplo() {
        // Productos de Tecnología
        Producto laptop = new Producto();
        laptop.setNombre("Laptop Gaming ASUS");
        laptop.setDescripcion("Laptop para gaming de alta gama con RTX 4060");
        laptop.setPrecio(1299.99);
        laptop.setStock(15);
        laptop.setCategoria("Tecnología");
        laptop.setCodigoInterno("LAPTOP-001");
        laptop.setImagenUrl("https://ejemplo.com/laptop-gaming.jpg");
        productoRepository.save(laptop);

        Producto smartphone = new Producto();
        smartphone.setNombre("iPhone 15 Pro");
        smartphone.setDescripcion("Smartphone de última generación con cámara profesional");
        smartphone.setPrecio(999.99);
        smartphone.setStock(25);
        smartphone.setCategoria("Tecnología");
        smartphone.setCodigoInterno("IPHONE-002");
        smartphone.setImagenUrl("https://ejemplo.com/iphone15.jpg");
        productoRepository.save(smartphone);

        // Productos de Ropa
        Producto camiseta = new Producto();
        camiseta.setNombre("Camiseta Premium");
        camiseta.setDescripcion("Camiseta de algodón 100% premium");
        camiseta.setPrecio(29.99);
        camiseta.setStock(50);
        camiseta.setCategoria("Ropa");
        camiseta.setCodigoInterno("CAMIS-003");
        camiseta.setImagenUrl("https://ejemplo.com/camiseta.jpg");
        productoRepository.save(camiseta);

        Producto jeans = new Producto();
        jeans.setNombre("Jeans Clásicos");
        jeans.setDescripcion("Jeans de alta calidad con estilo clásico");
        jeans.setPrecio(79.99);
        jeans.setStock(30);
        jeans.setCategoria("Ropa");
        jeans.setCodigoInterno("JEANS-004");
        jeans.setImagenUrl("https://ejemplo.com/jeans.jpg");
        productoRepository.save(jeans);

        // Productos de Hogar
        Producto lampara = new Producto();
        lampara.setNombre("Lámpara de Mesa LED");
        lampara.setDescripcion("Lámpara moderna con luz LED ajustable");
        lampara.setPrecio(45.99);
        lampara.setStock(20);
        lampara.setCategoria("Hogar");
        lampara.setCodigoInterno("LAMP-005");
        lampara.setImagenUrl("https://ejemplo.com/lampara.jpg");
        productoRepository.save(lampara);

        Producto sarten = new Producto();
        sarten.setNombre("Sartén Antiadherente");
        sarten.setDescripcion("Sartén profesional con recubrimiento antiadherente");
        sarten.setPrecio(35.99);
        sarten.setStock(40);
        sarten.setCategoria("Hogar");
        sarten.setCodigoInterno("SART-006");
        sarten.setImagenUrl("https://ejemplo.com/sarten.jpg");
        productoRepository.save(sarten);
    }

    private void actualizarProductosSinCodigoInterno() {
        try {
            // Usar SQL directo para actualizar productos con códigos temporales
            String sql = "SELECT id, nombre FROM producto WHERE codigo_interno LIKE 'TEMP-%'";
            List<Map<String, Object>> productosTemp = jdbcTemplate.queryForList(sql);
            
            if (!productosTemp.isEmpty()) {
                System.out.println("🔄 Actualizando " + productosTemp.size() + " productos con códigos temporales...");
                
                for (Map<String, Object> producto : productosTemp) {
                    Long id = (Long) producto.get("id");
                    String nombre = (String) producto.get("nombre");
                    String codigoGenerado = generarCodigoInterno(nombre, id);
                    
                    // Actualizar usando SQL directo
                    jdbcTemplate.update(
                        "UPDATE producto SET codigo_interno = ?, activo = true WHERE id = ?",
                        codigoGenerado, id
                    );
                }
                
                System.out.println("✅ Productos actualizados con códigos internos");
            } else {
                System.out.println("ℹ️ No hay productos que necesiten actualización de código interno");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar productos: " + e.getMessage());
        }
    }

    private String generarCodigoInterno(String nombre, Long id) {
        try {
            String codigo = nombre.toUpperCase()
                    .replaceAll("[^A-Z0-9]", "")
                    .substring(0, Math.min(6, nombre.length()));
            return codigo + "-" + String.format("%04d", id);
        } catch (Exception e) {
            // Fallback si hay algún problema con el nombre
            return "PROD-" + String.format("%04d", id);
        }
    }
} 