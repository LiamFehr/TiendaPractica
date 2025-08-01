# 📋 Documentación de Endpoints - Tienda Virtual

## 🔐 **Endpoints de Autenticación**

### 1. **Registro de Usuario**
```http
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
    "nombre": "Juan Pérez",
    "email": "juan@ejemplo.com",
    "password": "Password123!"
}
```

### 2. **Login**
```http
POST http://localhost:8081/api/auth/login
Content-Type: application/json

{
    "email": "admin@tienda.com",
    "password": "Admin123!"
}
```

### 3. **Registro de Administrador** (Requiere autenticación)
```http
POST http://localhost:8081/api/auth/register-admin
Authorization: Bearer {token}
Content-Type: application/json

{
    "nombre": "Admin Nuevo",
    "email": "admin2@tienda.com",
    "password": "Admin123!"
}
```

## 📦 **Endpoints de Productos**

### 4. **Obtener Todos los Productos**
```http
GET http://localhost:8081/api/productos
```

### 5. **Obtener Producto por ID**
```http
GET http://localhost:8081/api/productos/1
```

### 6. **Crear Producto** (Requiere rol ADMIN)
```http
POST http://localhost:8081/api/productos
Authorization: Bearer {token}
Content-Type: application/json

{
    "nombre": "Nuevo Producto",
    "descripcion": "Descripción del producto",
    "precio": 99.99,
    "stock": 50,
    "categoria": "Tecnología",
    "imagenUrl": "https://ejemplo.com/imagen.jpg"
}
```

### 7. **Actualizar Producto** (Requiere rol ADMIN)
```http
PUT http://localhost:8081/api/productos/1
Authorization: Bearer {token}
Content-Type: application/json

{
    "nombre": "Producto Actualizado",
    "descripcion": "Nueva descripción",
    "precio": 89.99,
    "stock": 45,
    "categoria": "Tecnología",
    "imagenUrl": "https://ejemplo.com/nueva-imagen.jpg"
}
```

### 8. **Eliminar Producto** (Requiere rol ADMIN)
```http
DELETE http://localhost:8081/api/productos/1
Authorization: Bearer {token}
```

### 9. **Buscar por Categoría**
```http
GET http://localhost:8081/api/productos/categoria/Tecnología
```

### 10. **Buscar por Nombre**
```http
GET http://localhost:8081/api/productos/buscar?nombre=laptop
```

### 11. **Buscar por Rango de Precio**
```http
GET http://localhost:8081/api/productos/precio?precioMin=50&precioMax=100
```

### 12. **Productos con Stock Bajo** (Requiere rol ADMIN)
```http
GET http://localhost:8081/api/productos/stock-bajo?limite=5
Authorization: Bearer {token}
```

### 13. **Obtener Todas las Categorías**
```http
GET http://localhost:8081/api/productos/categorias
```

## 🔧 **Credenciales por Defecto**

- **Email**: `admin@tienda.com`
- **Password**: `Admin123!`
- **Rol**: `ROLE_ADMIN`

## ⚠️ **Notas Importantes**

1. **Autenticación**: Para endpoints protegidos, incluye el header `Authorization: Bearer {token}`
2. **Token JWT**: Se obtiene del endpoint de login
3. **Roles**: 
   - `ROLE_USER`: Puede ver productos
   - `ROLE_ADMIN`: Puede crear, actualizar y eliminar productos
4. **Validaciones**: Los DTOs incluyen validaciones que deben cumplirse

## 🧪 **Pruebas Recomendadas**

### Secuencia de Pruebas:

1. **Registro de usuario nuevo**
2. **Login con credenciales por defecto**
3. **Obtener todos los productos** (sin token)
4. **Crear un producto** (con token de admin)
5. **Actualizar el producto creado**
6. **Buscar productos por categoría**
7. **Eliminar el producto**

### Herramientas de Prueba:
- **Postman**
- **cURL**
- **Insomnia**
- **Thunder Client** (VS Code)

## 🚨 **Posibles Errores y Soluciones**

### Error 401 (Unauthorized):
- Verificar que el token JWT sea válido
- Verificar que el token no haya expirado
- Verificar el formato del header Authorization

### Error 403 (Forbidden):
- Verificar que el usuario tenga el rol necesario
- Verificar que el token corresponda a un usuario con permisos

### Error 400 (Bad Request):
- Verificar que todos los campos requeridos estén presentes
- Verificar que los datos cumplan con las validaciones
- Verificar el formato JSON

### Error 404 (Not Found):
- Verificar que el ID del producto exista
- Verificar que la URL del endpoint sea correcta 