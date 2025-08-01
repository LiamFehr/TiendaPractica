#!/bin/bash

# Script de diagnóstico para la Tienda Virtual
# Asegúrate de que la aplicación esté corriendo en http://localhost:8081

BASE_URL="http://localhost:8081"
echo "🔍 Diagnóstico de la Tienda Virtual..."
echo "======================================"

# 1. Verificar estado de autenticación (sin token)
echo "1. Verificando estado de autenticación (sin token)..."
AUTH_STATUS=$(curl -s -X GET "$BASE_URL/api/diagnostic/auth-status")
echo "Respuesta: $AUTH_STATUS"
echo ""

# 2. Probar login con admin
echo "2. Probando login con admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tienda.com",
    "password": "Admin123!"
  }')
echo "Respuesta del login: $LOGIN_RESPONSE"
echo ""

# Extraer token del login
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token extraído: $TOKEN"
echo ""

# 3. Verificar estado de autenticación con token
echo "3. Verificando estado de autenticación (con token)..."
AUTH_STATUS_WITH_TOKEN=$(curl -s -X GET "$BASE_URL/api/diagnostic/auth-status" \
  -H "Authorization: Bearer $TOKEN")
echo "Respuesta: $AUTH_STATUS_WITH_TOKEN"
echo ""

# 4. Probar acceso de admin
echo "4. Probando acceso de administrador..."
ADMIN_TEST=$(curl -s -X GET "$BASE_URL/api/diagnostic/test-admin" \
  -H "Authorization: Bearer $TOKEN")
echo "Respuesta: $ADMIN_TEST"
echo ""

# 5. Probar acceso de usuario
echo "5. Probando acceso de usuario..."
USER_TEST=$(curl -s -X GET "$BASE_URL/api/diagnostic/test-user" \
  -H "Authorization: Bearer $TOKEN")
echo "Respuesta: $USER_TEST"
echo ""

# 6. Probar crear producto
echo "6. Probando crear producto..."
CREATE_PRODUCT=$(curl -s -X POST "$BASE_URL/api/productos" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Producto Test Diagnóstico",
    "descripcion": "Producto para diagnóstico",
    "precio": 99.99,
    "stock": 10,
    "categoria": "Test",
    "imagenUrl": "https://ejemplo.com/test.jpg"
  }')
echo "Respuesta crear producto: $CREATE_PRODUCT"
echo ""

# 7. Probar productos con stock bajo
echo "7. Probando productos con stock bajo..."
STOCK_BAJO=$(curl -s -X GET "$BASE_URL/api/productos/stock-bajo?limite=5" \
  -H "Authorization: Bearer $TOKEN")
echo "Respuesta stock bajo: $STOCK_BAJO"
echo ""

echo "✅ Diagnóstico completado!"
echo "Revisa las respuestas arriba para identificar el problema." 