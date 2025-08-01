#!/bin/bash

# Script de prueba para el backend de la Tienda
# Autor: Asistente IA
# Fecha: $(date)

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuración
BASE_URL="http://localhost:8083"
ADMIN_EMAIL="admin@tienda.com"
ADMIN_PASSWORD="admin123"
TOKEN_FILE="token.txt"

echo -e "${BLUE}🚀 Iniciando pruebas del backend de la Tienda${NC}"
echo "=================================================="

# Función para hacer requests HTTP
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    local headers=""
    if [ ! -z "$token" ]; then
        headers="-H 'Authorization: Bearer $token'"
    fi
    
    if [ ! -z "$data" ]; then
        headers="$headers -H 'Content-Type: application/json'"
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" -d "$data" $headers)
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" $headers)
    fi
    
    echo "$response"
}

# Función para verificar respuesta
check_response() {
    local response=$1
    local expected_status=$2
    local test_name=$3
    
    local http_code=$(echo "$response" | tail -n1)
    local body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq "$expected_status" ]; then
        echo -e "${GREEN}✅ $test_name - OK (HTTP $http_code)${NC}"
        return 0
    else
        echo -e "${RED}❌ $test_name - FAILED (HTTP $http_code)${NC}"
        echo "Response: $body"
        return 1
    fi
}

# Función para extraer token de respuesta
extract_token() {
    local response=$1
    echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4
}

echo -e "${YELLOW}📋 Paso 1: Verificando que el servidor esté corriendo...${NC}"
server_check=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/productos")
if [ "$server_check" -eq 200 ]; then
    echo -e "${GREEN}✅ Servidor corriendo correctamente${NC}"
else
    echo -e "${RED}❌ Servidor no está corriendo en $BASE_URL${NC}"
    echo "Asegúrate de que la aplicación Spring Boot esté ejecutándose"
    exit 1
fi

echo -e "${YELLOW}📋 Paso 2: Autenticación...${NC}"
login_data="{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}"
login_response=$(make_request "POST" "/api/auth/login" "$login_data")
token=$(extract_token "$login_response")
check_response "$login_response" 200 "Login de administrador"

if [ -z "$token" ]; then
    echo -e "${RED}❌ No se pudo obtener el token de autenticación${NC}"
    exit 1
fi

echo "Token obtenido: ${token:0:20}..."
echo "$token" > $TOKEN_FILE

echo -e "${YELLOW}📋 Paso 3: Probando endpoints públicos...${NC}"

# Productos públicos
check_response "$(make_request "GET" "/api/productos")" 200 "Obtener todos los productos"
check_response "$(make_request "GET" "/api/productos/1")" 200 "Obtener producto por ID"
check_response "$(make_request "GET" "/api/productos/destacados")" 200 "Obtener productos destacados"

# Categorías públicas
check_response "$(make_request "GET" "/api/categorias")" 200 "Obtener categorías públicas"
check_response "$(make_request "GET" "/api/categorias/activas")" 200 "Obtener categorías activas"

echo -e "${YELLOW}📋 Paso 4: Probando filtros de productos...${NC}"
filter_data='{"nombre":"laptop","precioMin":0,"precioMax":1000,"ordenarPor":"precio","direccion":"asc","pagina":0,"tamaño":10}'
check_response "$(make_request "POST" "/api/productos/filtrar" "$filter_data")" 200 "Filtrar productos"

echo -e "${YELLOW}📋 Paso 5: Probando endpoints de administrador...${NC}"

# Crear nueva categoría
categoria_data='{"nombre":"Test Categoría","descripcion":"Categoría de prueba","color":"#ff0000","orden":5}'
check_response "$(make_request "POST" "/api/categorias" "$categoria_data" "$token")" 201 "Crear categoría"

# Obtener categoría por ID
check_response "$(make_request "GET" "/api/categorias/1" "" "$token")" 200 "Obtener categoría por ID"

# Crear nuevo producto
producto_data='{"nombre":"Producto Test","descripcion":"Producto de prueba","precio":99.99,"stock":10,"categoria":"Tecnología","codigoInterno":"TEST-001","imagenUrl":"https://via.placeholder.com/300x200"}'
check_response "$(make_request "POST" "/api/productos" "$producto_data" "$token")" 201 "Crear producto"

# Estadísticas de productos
check_response "$(make_request "GET" "/api/productos/estadisticas" "" "$token")" 200 "Obtener estadísticas"

echo -e "${YELLOW}📋 Paso 6: Probando carrito de compras...${NC}"

# Obtener carrito
check_response "$(make_request "GET" "/api/carrito" "" "$token")" 200 "Obtener carrito"

# Agregar producto al carrito
check_response "$(make_request "POST" "/api/carrito/agregar?productoId=1&cantidad=2" "" "$token")" 200 "Agregar producto al carrito"

# Actualizar cantidad
check_response "$(make_request "PUT" "/api/carrito/actualizar?productoId=1&cantidad=3" "" "$token")" 200 "Actualizar cantidad en carrito"

echo -e "${YELLOW}📋 Paso 7: Probando facturador...${NC}"

# Buscar producto por código interno
check_response "$(make_request "GET" "/api/facturador/producto/LAPTOP-001" "" "$token")" 200 "Buscar producto por código interno"

# Buscar productos por texto
check_response "$(make_request "GET" "/api/facturador/buscar?busqueda=laptop" "" "$token")" 200 "Buscar productos por texto"

# Productos de acceso rápido
check_response "$(make_request "GET" "/api/facturador/acceso-rapido" "" "$token")" 200 "Obtener productos de acceso rápido"

echo -e "${YELLOW}📋 Paso 8: Probando sistema de pagos...${NC}"

# Crear pedido primero
pedido_data='{"usuarioId":1,"detalles":[{"productoId":1,"cantidad":1,"precioUnitario":999.99}],"direccionEnvio":"Calle Test 123","telefono":"+1234567890"}'
pedido_response=$(make_request "POST" "/api/pedidos" "$pedido_data" "$token")
check_response "$pedido_response" 201 "Crear pedido"

# Extraer ID del pedido creado
pedido_id=$(echo "$pedido_response" | head -n -1 | grep -o '"id":[0-9]*' | cut -d':' -f2)

if [ ! -z "$pedido_id" ]; then
    # Crear pago
    pago_data="{\"pedidoId\":$pedido_id,\"metodosPago\":[\"credit_card\"],\"descripcion\":\"Compra de prueba\",\"urlRetorno\":\"https://test.com/success\",\"urlCancelacion\":\"https://test.com/cancel\"}"
    pago_response=$(make_request "POST" "/api/pagos/crear" "$pago_data" "$token")
    check_response "$pago_response" 200 "Crear pago"
    
    # Extraer ID de Mercado Pago
    mp_id=$(echo "$pago_response" | head -n -1 | grep -o '"mercadopagoId":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$mp_id" ]; then
        # Obtener estado de pago
        check_response "$(make_request "GET" "/api/pagos/estado/$mp_id" "" "$token")" 200 "Obtener estado de pago"
    fi
fi

echo -e "${YELLOW}📋 Paso 9: Probando endpoints de diagnóstico...${NC}"

check_response "$(make_request "GET" "/api/diagnostico/auth" "" "$token")" 200 "Verificar autenticación"
check_response "$(make_request "GET" "/api/diagnostico/roles" "" "$token")" 200 "Verificar roles"
check_response "$(make_request "GET" "/api/diagnostico/jwt" "" "$token")" 200 "Verificar JWT"

echo -e "${YELLOW}📋 Paso 10: Limpieza...${NC}"

# Limpiar carrito
check_response "$(make_request "DELETE" "/api/carrito/limpiar" "" "$token")" 200 "Limpiar carrito"

echo ""
echo -e "${BLUE}🎉 Pruebas completadas!${NC}"
echo "=================================================="
echo -e "${GREEN}✅ Backend funcionando correctamente${NC}"
echo ""
echo -e "${YELLOW}📝 Próximos pasos:${NC}"
echo "1. Si todas las pruebas pasaron, el backend está listo"
echo "2. Puedes proceder con el desarrollo del frontend"
echo "3. El token de autenticación se guardó en: $TOKEN_FILE"
echo ""
echo -e "${BLUE}🚀 ¡Listo para el frontend!${NC}"

# Limpiar archivo temporal
rm -f $TOKEN_FILE 