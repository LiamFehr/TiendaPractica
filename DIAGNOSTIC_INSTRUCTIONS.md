# 🔍 Instrucciones de Diagnóstico - Problema de Autenticación

## 🚨 **Problema Identificado**
No puedes crear/modificar productos como admin ni acceder a endpoints protegidos, aunque el login funciona.

## 📋 **Pasos para Diagnosticar**

### **Paso 1: Reiniciar la Aplicación**
```bash
# Detén la aplicación actual (Ctrl+C)
# Luego reinicia
./mvnw spring-boot:run
```

### **Paso 2: Ejecutar el Script de Diagnóstico**
```bash
# Dar permisos de ejecución
chmod +x diagnostic_test.sh

# Ejecutar diagnóstico
./diagnostic_test.sh
```

### **Paso 3: Verificar Manualmente**

#### **3.1 Login y Obtener Token**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tienda.com",
    "password": "Admin123!"
  }'
```

#### **3.2 Verificar Estado de Autenticación**
```bash
# Reemplaza {TOKEN} con el token real
curl -X GET http://localhost:8081/api/diagnostic/auth-status \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta esperada:**
```json
{
  "authenticated": true,
  "username": "admin@tienda.com",
  "authorities": ["ROLE_ADMIN"],
  "userDetails": {
    "username": "admin@tienda.com",
    "authorities": ["ROLE_ADMIN"],
    "enabled": true,
    "accountNonExpired": true,
    "credentialsNonExpired": true,
    "accountNonLocked": true
  }
}
```

#### **3.3 Probar Acceso de Admin**
```bash
curl -X GET http://localhost:8081/api/diagnostic/test-admin \
  -H "Authorization: Bearer {TOKEN}"
```

#### **3.4 Probar Crear Producto**
```bash
curl -X POST http://localhost:8081/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "nombre": "Producto Test",
    "descripcion": "Descripción de prueba",
    "precio": 99.99,
    "stock": 10,
    "categoria": "Test",
    "imagenUrl": "https://ejemplo.com/test.jpg"
  }'
```

## 🔍 **Posibles Causas y Soluciones**

### **Causa 1: Token Mal Formateado**
**Síntoma**: Error 401 en todos los endpoints protegidos
**Solución**: Verificar que el token se esté enviando correctamente

### **Causa 2: Token Expirado**
**Síntoma**: Error 401 después de un tiempo
**Solución**: Hacer login nuevamente para obtener un token fresco

### **Causa 3: Problema con Roles**
**Síntoma**: Error 403 en endpoints de admin
**Solución**: Verificar que el usuario tenga el rol ROLE_ADMIN

### **Causa 4: Problema con JWT Secret**
**Síntoma**: Token válido pero no se reconoce
**Solución**: Reiniciar la aplicación para regenerar la clave

## 📝 **Verificaciones en la Consola**

Cuando ejecutes las pruebas, deberías ver en la consola de la aplicación:

```
✅ JWT Token válido para usuario: admin@tienda.com
   Roles: [ROLE_ADMIN]
```

Si ves algo diferente, hay un problema con la autenticación.

## 🛠️ **Comandos de Debugging**

### **Verificar Base de Datos**
```sql
-- Conectar a PostgreSQL y ejecutar:
SELECT email, role FROM usuario WHERE email = 'admin@tienda.com';
```

### **Verificar Logs de Spring Security**
En `application.properties` ya tienes:
```properties
logging.level.org.springframework.security=DEBUG
```

## 📊 **Resultados Esperados**

### **Login Exitoso:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### **Estado de Autenticación:**
```json
{
  "authenticated": true,
  "username": "admin@tienda.com",
  "authorities": ["ROLE_ADMIN"]
}
```

### **Crear Producto Exitoso:**
```json
{
  "id": 1,
  "nombre": "Producto Test",
  "descripcion": "Descripción de prueba",
  "precio": 99.99,
  "stock": 10,
  "categoria": "Test",
  "imagenUrl": "https://ejemplo.com/test.jpg",
  "fechaCreacion": "2024-01-01T10:00:00",
  "activo": true
}
```

## 🚨 **Si el Problema Persiste**

1. **Revisa los logs** de la aplicación en la consola
2. **Verifica la base de datos** para confirmar que el usuario admin existe
3. **Prueba con un usuario nuevo** registrándolo como admin
4. **Verifica la configuración** de PostgreSQL

## 📞 **Información para Reportar**

Si necesitas ayuda adicional, proporciona:

1. **Respuesta del endpoint `/api/diagnostic/auth-status`**
2. **Logs de la consola** durante las pruebas
3. **Respuesta exacta** de los endpoints que fallan
4. **Versión de Java** y **Spring Boot** que estás usando 