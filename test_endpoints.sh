#!/bin/bash

# Script para probar los endpoints de la Tienda Virtual
# Asegúrate de que la aplicación esté corriendo en http://localhost:8081

BASE_URL="http://localhost:8081"
echo "🧪 Probando endpoints de la Tienda Virtual..."
echo "=============================================="

# 1. Probar registro de usuario
echo "1. Probando registro de usuario..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Usuario Test",
    "email": "test@ejemplo.com",
    "password": "Test123!"
  }')
echo "Respuesta: $REGISTER_RESPONSE"
echo ""

# 2. Probar login con admin por defecto
echo "2. Probando login con admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tienda.com",
    "password": "Admin123!"
  }')
echo "Respuesta: $LOGIN_RESPONSE"
echo ""

# Extraer token del login
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token obtenido: $TOKEN"
echo ""

# 3. Probar obtener todos los productos (sin token)
echo "3. Probando obtener todos los productos..."
PRODUCTS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/productos")
echo "Respuesta: $PRODUCTS_RESPONSE"
echo ""

# 4. Probar crear producto (con token)
echo "4. Probando crear producto..."
CREATE_PRODUCT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/productos" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Producto Test",
    "descripcion": "Producto de prueba",
    "precio": 99.99,
    "stock": 10,
    "categoria": "Test",
    "imagenUrl": "https://ejemplo.com/test.jpg"
  }')
echo "Respuesta: $CREATE_PRODUCT_RESPONSE"
echo ""

# 5. Probar obtener categorías
echo "5. Probando obtener categorías..."
CATEGORIES_RESPONSE=$(curl -s -X GET "$BASE_URL/api/productos/categorias")
echo "Respuesta: $CATEGORIES_RESPONSE"
echo ""

# 6. Probar búsqueda por categoría
echo "6. Probando búsqueda por categoría..."
CATEGORY_SEARCH_RESPONSE=$(curl -s -X GET "$BASE_URL/api/productos/categoria/Tecnología")
echo "Respuesta: $CATEGORY_SEARCH_RESPONSE"
echo ""

echo "✅ Pruebas completadas!"
echo "Revisa las respuestas arriba para verificar que todo funcione correctamente." 