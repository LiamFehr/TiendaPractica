# 🛒 Tienda Virtual - API REST

Una API REST completa para una tienda virtual desarrollada con Spring Boot, Spring Security y JWT.

## 🚀 **Características**

- ✅ **Autenticación JWT** con roles de usuario y administrador
- ✅ **CRUD completo** de productos
- ✅ **Búsquedas avanzadas** por categoría, nombre y precio
- ✅ **Validaciones** de datos con Bean Validation
- ✅ **Eliminación lógica** de productos
- ✅ **Base de datos PostgreSQL** con JPA/Hibernate
- ✅ **Documentación completa** de endpoints

## 🛠️ **Tecnologías Utilizadas**

- **Spring Boot 3.x**
- **Spring Security 6.x**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (JSON Web Tokens)**
- **Maven**
- **Lombok**

## 📋 **Requisitos Previos**

- Java 17 o superior
- PostgreSQL 12 o superior
- Maven 3.6 o superior

## ⚙️ **Configuración**

### 1. **Base de Datos**

Crea una base de datos PostgreSQL:

```sql
CREATE DATABASE tienda_virtual;
```

### 2. **Configuración de la Aplicación**

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/tienda_virtual
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

### 3. **Ejecutar la Aplicación**

```bash
# Compilar y ejecutar
./mvnw spring-boot:run

# O compilar primero
./mvnw clean compile
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

## 🔐 **Autenticación**

### Credenciales por Defecto

- **Email**: `admin@tienda.com`
- **Password**: `Admin123!`
- **Rol**: `ROLE_ADMIN`

### Flujo de Autenticación

1. **Registro**: `POST /api/auth/register`
2. **Login**: `POST /api/auth/login`
3. **Usar token**: Incluir `Authorization: Bearer {token}` en headers

## 📚 **Endpoints Disponibles**

### **Autenticación**
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login de usuario
- `POST /api/auth/register-admin` - Registro de administrador (requiere autenticación)

### **Productos**
- `GET /api/productos` - Obtener todos los productos
- `GET /api/productos/{id}` - Obtener producto por ID
- `POST /api/productos` - Crear producto (ADMIN)
- `PUT /api/productos/{id}` - Actualizar producto (ADMIN)
- `DELETE /api/productos/{id}` - Eliminar producto (ADMIN)

### **Búsquedas**
- `GET /api/productos/categoria/{categoria}` - Buscar por categoría
- `GET /api/productos/buscar?nombre={nombre}` - Buscar por nombre
- `GET /api/productos/precio?precioMin={min}&precioMax={max}` - Buscar por rango de precio
- `GET /api/productos/stock-bajo?limite={limite}` - Productos con stock bajo (ADMIN)
- `GET /api/productos/categorias` - Obtener todas las categorías

## 🧪 **Pruebas**

### Usando el Script de Pruebas

```bash
# Dar permisos de ejecución
chmod +x test_endpoints.sh

# Ejecutar pruebas
./test_endpoints.sh
```

### Usando Postman

1. Importa la colección de Postman (si está disponible)
2. Configura la variable de entorno `baseUrl` como `http://localhost:8081`
3. Ejecuta las pruebas en orden

### Usando cURL

Ejemplos básicos:

```bash
# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@tienda.com","password":"Admin123!"}'

# Obtener productos
curl -X GET http://localhost:8081/api/productos
```

## 🔧 **Estructura del Proyecto**

```
src/main/java/com/Ejemplo/tienda/
├── Config/                 # Configuraciones
│   ├── DataInitializer.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── Controller/             # Controladores REST
│   ├── AuthController.java
│   ├── ProductoController.java
│   └── GlobalExceptionHandler.java
├── Dto/                   # Objetos de transferencia de datos
│   ├── ProductoDTO.java
│   ├── ProductoCrearDTO.java
│   └── ...
├── Models/                # Entidades JPA
│   ├── Producto.java
│   ├── Usuario.java
│   └── ...
├── Repository/            # Repositorios JPA
│   ├── ProductoRepository.java
│   └── ...
├── Security/              # Configuración de seguridad
│   ├── JwtFilter.java
│   └── JwtUtils.java
└── Service/               # Lógica de negocio
    ├── interfaces/
    └── impl/
```

## 🚨 **Solución de Problemas**

### Error de Conexión a Base de Datos
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales en `application.properties`
- Verificar que la base de datos exista

### Error 401 (Unauthorized)
- Verificar que el token JWT sea válido
- Verificar que el token no haya expirado
- Verificar el formato del header Authorization

### Error 403 (Forbidden)
- Verificar que el usuario tenga el rol necesario
- Verificar que el token corresponda a un usuario con permisos

### Error 400 (Bad Request)
- Verificar que todos los campos requeridos estén presentes
- Verificar que los datos cumplan con las validaciones

## 📝 **Logs y Debugging**

La aplicación está configurada con logs detallados. Revisa la consola para:

- Consultas SQL ejecutadas
- Errores de autenticación
- Errores de validación
- Información de seguridad

## 🤝 **Contribución**

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 **Licencia**

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 **Soporte**

Si tienes problemas o preguntas:

1. Revisa la documentación de endpoints en `ENDPOINTS_TESTING.md`
2. Ejecuta el script de pruebas `test_endpoints.sh`
3. Revisa los logs de la aplicación
4. Abre un issue en el repositorio 