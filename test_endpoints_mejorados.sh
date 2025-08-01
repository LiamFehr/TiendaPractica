#!/bin/bash

# Script para probar los endpoints mejorados de la API
# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8082"
TOKEN=""

echo -e "${BLUE}🚀 Probando API Mejorada - Tienda Virtual${NC}"
echo "=================================================="

# Función para hacer requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "\n${YELLOW}📋 $description${NC}"
    echo "Endpoint: $method $BASE_URL$endpoint"
    
    if [ "$method" = "POST" ] || [ "$method" = "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $TOKEN" \
            -d "$data" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Authorization: Bearer $TOKEN" \
            "$BASE_URL$endpoint")
    fi
    
    # Separar respuesta y código HTTP
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ Éxito ($http_code)${NC}"
        echo "Respuesta: $response_body" | head -c 200
        if [ ${#response_body} -gt 200 ]; then
            echo "..."
        fi
    else
        echo -e "${RED}❌ Error ($http_code)${NC}"
        echo "Respuesta: $response_body"
    fi
}

# 1. Login para obtener token
echo -e "\n${BLUE}🔐 1. Autenticación${NC}"
echo "-------------------"

login_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"email": "admin@tienda.com", "password": "Admin123!"}' \
    "$BASE_URL/api/auth/login")

if echo "$login_response" | grep -q "token"; then
    TOKEN=$(echo "$login_response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo -e "${GREEN}✅ Login exitoso${NC}"
    echo "Token obtenido: ${TOKEN:0:20}..."
else
    echo -e "${RED}❌ Error en login${NC}"
    echo "Respuesta: $login_response"
    exit 1
fi

# 2. Endpoints básicos
echo -e "\n${BLUE}📦 2. Endpoints Básicos${NC}"
echo "----------------------"

make_request "GET" "/api/productos" "" "Obtener todos los productos"
make_request "GET" "/api/productos/1" "" "Obtener producto por ID"
make_request "GET" "/api/productos/categorias" "" "Obtener categorías"

# 3. Endpoints paginados
echo -e "\n${BLUE}📄 3. Endpoints Paginados${NC}"
echo "------------------------"

make_request "GET" "/api/productos/paginado?pagina=0&tamanio=3&ordenarPor=precio&direccion=desc" "" "Productos paginados (precio descendente)"
make_request "GET" "/api/productos/paginado?pagina=0&tamanio=3&ordenarPor=nombre&direccion=asc" "" "Productos paginados (nombre ascendente)"
make_request "GET" "/api/productos/categoria/Tecnología/paginado?pagina=0&tamanio=2" "" "Por categoría paginado"
make_request "GET" "/api/productos/buscar/paginado?nombre=laptop&pagina=0&tamanio=5" "" "Por nombre paginado"
make_request "GET" "/api/productos/precio/paginado?precioMin=100&precioMax=1000&pagina=0&tamanio=5" "" "Por precio paginado"
make_request "GET" "/api/productos/destacados?pagina=0&tamanio=3" "" "Productos destacados"

# 4. Filtros avanzados
echo -e "\n${BLUE}🔍 4. Filtros Avanzados${NC}"
echo "------------------------"

# Filtro completo
make_request "POST" "/api/productos/filtrar" '{
  "categoria": "Tecnología",
  "precioMin": 100.0,
  "precioMax": 2000.0,
  "nombre": "laptop",
  "stockMin": 1,
  "ordenarPor": "precio",
  "direccion": "asc",
  "pagina": 0,
  "tamanio": 5
}' "Filtros avanzados completos"

# Solo categoría
make_request "POST" "/api/productos/filtrar" '{
  "categoria": "Tecnología",
  "pagina": 0,
  "tamanio": 5
}' "Filtros solo por categoría"

# Solo precio
make_request "POST" "/api/productos/filtrar" '{
  "precioMin": 500.0,
  "precioMax": 1500.0,
  "ordenarPor": "precio",
  "direccion": "desc",
  "pagina": 0,
  "tamanio": 5
}' "Filtros solo por precio"

# Solo nombre
make_request "POST" "/api/productos/filtrar" '{
  "nombre": "pro",
  "ordenarPor": "nombre",
  "direccion": "asc",
  "pagina": 0,
  "tamanio": 5
}' "Filtros solo por nombre"

# 5. Estadísticas
echo -e "\n${BLUE}📊 5. Estadísticas${NC}"
echo "----------------"

make_request "GET" "/api/productos/estadisticas" "" "Estadísticas generales"
make_request "GET" "/api/productos/estadisticas/categoria/Tecnología" "" "Estadísticas por categoría"

# 6. Búsquedas simples (compatibilidad)
echo -e "\n${BLUE}🔍 6. Búsquedas Simples (Compatibilidad)${NC}"
echo "----------------------------------------"

make_request "GET" "/api/productos/buscar-simple?nombre=laptop" "" "Búsqueda simple por nombre"
make_request "GET" "/api/productos/precio-simple?precioMin=100&precioMax=1000" "" "Búsqueda simple por precio"
make_request "GET" "/api/productos/stock-bajo?limite=5" "" "Productos con stock bajo"

# 7. Diagnóstico
echo -e "\n${BLUE}🔍 7. Diagnóstico${NC}"
echo "----------------"

make_request "GET" "/api/diagnostic/auth-status" "" "Estado de autenticación"
make_request "GET" "/api/diagnostic/test-admin" "" "Test acceso admin"

echo -e "\n${GREEN}🎉 ¡Pruebas completadas!${NC}"
echo "=================================================="
echo -e "${BLUE}📋 Resumen de endpoints probados:${NC}"
echo "✅ Autenticación"
echo "✅ Endpoints básicos"
echo "✅ Paginación"
echo "✅ Filtros avanzados"
echo "✅ Estadísticas"
echo "✅ Búsquedas simples"
echo "✅ Diagnóstico"
echo ""
echo -e "${YELLOW}💡 Para más detalles, revisa la documentación en ENDPOINTS_MEJORADOS.md${NC}" 