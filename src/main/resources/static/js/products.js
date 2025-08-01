// Products Module for Tienda Online

// Products state
let products = [];
let categories = [];
let currentFilters = {
    pagina: 0,
    tamaño: 12,
    ordenarPor: 'nombre',
    direccion: 'asc'
};

// Initialize products module when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    setupProductEventListeners();
});

// Set up product event listeners
function setupProductEventListeners() {
    // Price range filters
    const precioMin = document.getElementById('precioMin');
    const precioMax = document.getElementById('precioMax');
    
    if (precioMin) {
        precioMin.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                aplicarFiltros();
            }
        });
    }
    
    if (precioMax) {
        precioMax.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                aplicarFiltros();
            }
        });
    }
}

// Load products with filters
async function loadProductsWithFilters(filters = {}) {
    try {
        showLoading(true);
        
        // Merge current filters with new filters
        const finalFilters = {
            ...currentFilters,
            ...filters
        };
        
        const response = await fetch(`${app.API_BASE_URL}/productos/filtrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(finalFilters)
        });
        
        if (response.ok) {
            const data = await response.json();
            products = data.contenido;
            currentFilters = finalFilters;
            
            displayProducts(products);
            setupPagination(data.totalPaginas, data.numeroPagina);
            
            return data;
        } else {
            throw new Error('Error al cargar productos');
        }
    } catch (error) {
        console.error('Error loading products:', error);
        app.showError('Error al cargar los productos');
        displayEmptyState();
    } finally {
        showLoading(false);
    }
}

// Display products in grid
function displayProducts(productsToShow) {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;
    
    if (!productsToShow || productsToShow.length === 0) {
        displayEmptyState();
        return;
    }
    
    grid.innerHTML = '';
    
    productsToShow.forEach((product, index) => {
        const productCard = createProductCard(product);
        grid.appendChild(productCard);
        
        // Add staggered animation
        setTimeout(() => {
            productCard.classList.add('fade-in');
        }, index * 100);
    });
}

// Create product card element
function createProductCard(product) {
    const col = document.createElement('div');
    col.className = 'col-lg-3 col-md-4 col-sm-6 mb-4';
    
    col.innerHTML = `
        <div class="card product-card h-100 shadow-custom">
            <div class="position-relative">
                <img src="${product.imagenUrl || 'https://via.placeholder.com/300x200?text=Sin+Imagen'}" 
                     class="card-img-top" alt="${product.nombre}">
                ${product.stock <= 5 && product.stock > 0 ? `
                    <span class="position-absolute top-0 end-0 badge bg-warning text-dark m-2">
                        ¡Pocas unidades!
                    </span>
                ` : ''}
                ${product.stock === 0 ? `
                    <span class="position-absolute top-0 end-0 badge bg-danger m-2">
                        Sin stock
                    </span>
                ` : ''}
            </div>
            <div class="card-body d-flex flex-column">
                <span class="category-badge">${product.categoria || 'Sin categoría'}</span>
                <h5 class="card-title">${product.nombre}</h5>
                <p class="card-text text-muted">${product.descripcion || 'Sin descripción'}</p>
                <div class="mt-auto">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="price">$${product.precio.toFixed(2)}</span>
                        <span class="stock ${product.stock === 0 ? 'text-danger' : ''}">
                            Stock: ${product.stock}
                        </span>
                    </div>
                    <div class="d-grid gap-2">
                        <button class="btn btn-primary" 
                                onclick="addToCart(${product.id})"
                                ${product.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-cart-plus me-2"></i>
                            ${product.stock === 0 ? 'Sin stock' : 'Agregar al Carrito'}
                        </button>
                        <button class="btn btn-outline-secondary" onclick="viewProductDetails(${product.id})">
                            <i class="fas fa-eye me-2"></i>Ver Detalles
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    return col;
}

// Display empty state
function displayEmptyState() {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;
    
    grid.innerHTML = `
        <div class="col-12">
            <div class="empty-state">
                <i class="fas fa-box-open"></i>
                <h3>No se encontraron productos</h3>
                <p>Intenta ajustar los filtros o busca algo diferente.</p>
                <button class="btn btn-primary" onclick="clearAllFilters()">
                    <i class="fas fa-refresh me-2"></i>Limpiar Filtros
                </button>
            </div>
        </div>
    `;
}

// Setup pagination
function setupPagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    
    pagination.innerHTML = '';
    
    if (totalPages <= 1) return;
    
    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = `
        <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </a>
    `;
    pagination.appendChild(prevLi);
    
    // Page numbers
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `
            <a class="page-link" href="#" onclick="changePage(${i})">${i + 1}</a>
        `;
        pagination.appendChild(li);
    }
    
    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = `
        <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </a>
    `;
    pagination.appendChild(nextLi);
}

// Change page
function changePage(page) {
    if (page < 0) return;
    
    currentFilters.pagina = page;
    loadProductsWithFilters();
    
    // Scroll to top smoothly
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Search products
function searchProducts(searchTerm) {
    if (!searchTerm || searchTerm.trim() === '') {
        clearSearch();
        return;
    }
    
    currentFilters.nombre = searchTerm.trim();
    currentFilters.pagina = 0;
    loadProductsWithFilters();
}

// Clear search
function clearSearch() {
    delete currentFilters.nombre;
    currentFilters.pagina = 0;
    loadProductsWithFilters();
}

// Filter by category
function filterByCategory(category) {
    if (!category || category === '') {
        delete currentFilters.categoria;
    } else {
        currentFilters.categoria = category;
    }
    currentFilters.pagina = 0;
    loadProductsWithFilters();
}

// Apply price filters
function aplicarFiltros() {
    const precioMin = document.getElementById('precioMin');
    const precioMax = document.getElementById('precioMax');
    
    if (precioMin && precioMin.value) {
        currentFilters.precioMin = parseFloat(precioMin.value);
    } else {
        delete currentFilters.precioMin;
    }
    
    if (precioMax && precioMax.value) {
        currentFilters.precioMax = parseFloat(precioMax.value);
    } else {
        delete currentFilters.precioMax;
    }
    
    currentFilters.pagina = 0;
    loadProductsWithFilters();
}

// Clear all filters
function clearAllFilters() {
    currentFilters = {
        pagina: 0,
        tamaño: 12,
        ordenarPor: 'nombre',
        direccion: 'asc'
    };
    
    // Reset form inputs
    const searchInput = document.getElementById('searchInput');
    const categoriaFilter = document.getElementById('categoriaFilter');
    const ordenFilter = document.getElementById('ordenFilter');
    const precioMin = document.getElementById('precioMin');
    const precioMax = document.getElementById('precioMax');
    
    if (searchInput) searchInput.value = '';
    if (categoriaFilter) categoriaFilter.value = '';
    if (ordenFilter) ordenFilter.value = 'nombre,asc';
    if (precioMin) precioMin.value = '';
    if (precioMax) precioMax.value = '';
    
    loadProductsWithFilters();
}

// Show loading spinner
function showLoading(show) {
    const spinner = document.getElementById('loadingSpinner');
    const grid = document.getElementById('productsGrid');
    
    if (spinner) spinner.style.display = show ? 'block' : 'none';
    if (grid) grid.style.display = show ? 'none' : 'block';
}

// View product details
function viewProductDetails(productId) {
    // For now, show a modal with product details
    // In a real app, this would navigate to a product detail page
    
    const product = products.find(p => p.id === productId);
    if (!product) {
        app.showError('Producto no encontrado');
        return;
    }
    
    // Create and show product detail modal
    const modalHtml = `
        <div class="modal fade" id="productDetailModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${product.nombre}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <img src="${product.imagenUrl || 'https://via.placeholder.com/400x300?text=Sin+Imagen'}" 
                                     class="img-fluid rounded" alt="${product.nombre}">
                            </div>
                            <div class="col-md-6">
                                <h4>${product.nombre}</h4>
                                <p class="text-muted">${product.descripcion || 'Sin descripción'}</p>
                                <div class="mb-3">
                                    <span class="badge bg-info">${product.categoria || 'Sin categoría'}</span>
                                </div>
                                <div class="mb-3">
                                    <h3 class="text-primary">$${product.precio.toFixed(2)}</h3>
                                </div>
                                <div class="mb-3">
                                    <strong>Stock:</strong> 
                                    <span class="${product.stock === 0 ? 'text-danger' : ''}">${product.stock} unidades</span>
                                </div>
                                ${product.codigoInterno ? `
                                    <div class="mb-3">
                                        <strong>Código Interno:</strong> ${product.codigoInterno}
                                    </div>
                                ` : ''}
                                <button class="btn btn-primary btn-lg w-100" 
                                        onclick="addToCart(${product.id})"
                                        ${product.stock === 0 ? 'disabled' : ''}>
                                    <i class="fas fa-cart-plus me-2"></i>
                                    ${product.stock === 0 ? 'Sin stock' : 'Agregar al Carrito'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('productDetailModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add new modal to body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('productDetailModal'));
    modal.show();
    
    // Remove modal from DOM when hidden
    document.getElementById('productDetailModal').addEventListener('hidden.bs.modal', function() {
        this.remove();
    });
}

// Load categories
async function loadCategories() {
    try {
        const response = await fetch(`${app.API_BASE_URL}/categorias`);
        if (response.ok) {
            categories = await response.json();
            populateCategoryFilter(categories);
            return categories;
        }
    } catch (error) {
        console.error('Error loading categories:', error);
    }
}

// Populate category filter dropdown
function populateCategoryFilter(categories) {
    const filter = document.getElementById('categoriaFilter');
    if (!filter) return;
    
    // Keep the "Todas las categorías" option
    filter.innerHTML = '<option value="">Todas las categorías</option>';
    
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.nombre;
        option.textContent = category.nombre;
        filter.appendChild(option);
    });
}

// Export functions for use in other modules
window.products = {
    loadProductsWithFilters,
    searchProducts,
    filterByCategory,
    aplicarFiltros,
    clearAllFilters,
    viewProductDetails,
    loadCategories
}; 