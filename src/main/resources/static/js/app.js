// Tienda Online - Main Application JavaScript

// Global variables
const API_BASE_URL = 'http://localhost:8083/api';
let currentUser = null;
let currentPage = 0;
let totalPages = 0;
let currentFilters = {};

// Initialize application when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Tienda Online - Aplicación iniciada');
    
    // Initialize all modules
    initializeApp();
    
    // Set up event listeners
    setupEventListeners();
    
    // Load initial data
    loadInitialData();
});

// Initialize the application
function initializeApp() {
    // Check if user is logged in
    checkAuthStatus();
    
    // Load categories
    loadCategories();
    
    // Load products
    loadProducts();
    
    // Initialize cart
    initializeCart();
}

// Set up event listeners
function setupEventListeners() {
    // Search form
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchTerm = document.getElementById('searchInput').value;
            if (searchTerm.trim()) {
                searchProducts(searchTerm);
            }
        });
    }
    
    // Cart button
    const cartBtn = document.getElementById('cartBtn');
    if (cartBtn) {
        cartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            showCart();
        });
    }
    
    // Logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }
    
    // Category filter
    const categoriaFilter = document.getElementById('categoriaFilter');
    if (categoriaFilter) {
        categoriaFilter.addEventListener('change', function() {
            currentFilters.categoria = this.value;
            currentPage = 0;
            loadProducts();
        });
    }
    
    // Order filter
    const ordenFilter = document.getElementById('ordenFilter');
    if (ordenFilter) {
        ordenFilter.addEventListener('change', function() {
            const [campo, direccion] = this.value.split(',');
            currentFilters.ordenarPor = campo;
            currentFilters.direccion = direccion;
            currentPage = 0;
            loadProducts();
        });
    }
}

// Load initial data
function loadInitialData() {
    // Show loading spinner
    showLoading(true);
    
    // Load products and categories in parallel
    Promise.all([
        loadProducts(),
        loadCategories()
    ]).finally(() => {
        showLoading(false);
    });
}

// Load categories
async function loadCategories() {
    try {
        const response = await fetch(`${API_BASE_URL}/categorias`);
        if (response.ok) {
            const categories = await response.json();
            populateCategoriesMenu(categories);
            populateCategoryFilter(categories);
        }
    } catch (error) {
        console.error('Error loading categories:', error);
        showError('Error al cargar las categorías');
    }
}

// Populate categories menu
function populateCategoriesMenu(categories) {
    const menu = document.getElementById('categoriasMenu');
    if (!menu) return;
    
    menu.innerHTML = '';
    
    categories.forEach(category => {
        const li = document.createElement('li');
        li.innerHTML = `
            <a class="dropdown-item" href="#" onclick="filterByCategory('${category.nombre}')">
                <span class="badge" style="background-color: ${category.color}">${category.nombre}</span>
            </a>
        `;
        menu.appendChild(li);
    });
}

// Populate category filter
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

// Load products
async function loadProducts() {
    try {
        showLoading(true);
        
        // Build query parameters
        const params = new URLSearchParams({
            pagina: currentPage,
            tamaño: 12,
            ...currentFilters
        });
        
        const response = await fetch(`${API_BASE_URL}/productos/filtrar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pagina: currentPage,
                tamaño: 12,
                ...currentFilters
            })
        });
        
        if (response.ok) {
            const data = await response.json();
            displayProducts(data.contenido);
            setupPagination(data.totalPaginas, data.numeroPagina);
            totalPages = data.totalPaginas;
        } else {
            throw new Error('Error al cargar productos');
        }
    } catch (error) {
        console.error('Error loading products:', error);
        showError('Error al cargar los productos');
        displayEmptyState();
    } finally {
        showLoading(false);
    }
}

// Display products
function displayProducts(products) {
    const grid = document.getElementById('productsGrid');
    if (!grid) return;
    
    if (!products || products.length === 0) {
        displayEmptyState();
        return;
    }
    
    grid.innerHTML = '';
    
    products.forEach(product => {
        const productCard = createProductCard(product);
        grid.appendChild(productCard);
    });
    
    // Add fade-in animation
    setTimeout(() => {
        const cards = grid.querySelectorAll('.product-card');
        cards.forEach((card, index) => {
            setTimeout(() => {
                card.classList.add('fade-in');
            }, index * 100);
        });
    }, 100);
}

// Create product card
function createProductCard(product) {
    const col = document.createElement('div');
    col.className = 'col-lg-3 col-md-4 col-sm-6 mb-4';
    
    col.innerHTML = `
        <div class="card product-card h-100 shadow-custom">
            <img src="${product.imagenUrl || 'https://via.placeholder.com/300x200?text=Sin+Imagen'}" 
                 class="card-img-top" alt="${product.nombre}">
            <div class="card-body d-flex flex-column">
                <span class="category-badge">${product.categoria || 'Sin categoría'}</span>
                <h5 class="card-title">${product.nombre}</h5>
                <p class="card-text text-muted">${product.descripcion || 'Sin descripción'}</p>
                <div class="mt-auto">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="price">$${product.precio.toFixed(2)}</span>
                        <span class="stock">Stock: ${product.stock}</span>
                    </div>
                    <div class="d-grid gap-2">
                        <button class="btn btn-primary" onclick="addToCart(${product.id})">
                            <i class="fas fa-cart-plus me-2"></i>Agregar al Carrito
                        </button>
                        <button class="btn btn-outline-secondary" onclick="viewProduct(${product.id})">
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
                <button class="btn btn-primary" onclick="clearFilters()">
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
    if (page < 0 || page >= totalPages) return;
    currentPage = page;
    loadProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Search products
function searchProducts(searchTerm) {
    currentFilters.nombre = searchTerm;
    currentPage = 0;
    loadProducts();
}

// Filter by category
function filterByCategory(category) {
    currentFilters.categoria = category;
    currentPage = 0;
    loadProducts();
}

// Apply filters
function aplicarFiltros() {
    const precioMin = document.getElementById('precioMin').value;
    const precioMax = document.getElementById('precioMax').value;
    
    if (precioMin) currentFilters.precioMin = parseFloat(precioMin);
    if (precioMax) currentFilters.precioMax = parseFloat(precioMax);
    
    currentPage = 0;
    loadProducts();
}

// Clear filters
function clearFilters() {
    currentFilters = {};
    currentPage = 0;
    
    // Reset form inputs
    document.getElementById('searchInput').value = '';
    document.getElementById('categoriaFilter').value = '';
    document.getElementById('ordenFilter').value = 'nombre,asc';
    document.getElementById('precioMin').value = '';
    document.getElementById('precioMax').value = '';
    
    loadProducts();
}

// Show loading spinner
function showLoading(show) {
    const spinner = document.getElementById('loadingSpinner');
    const grid = document.getElementById('productsGrid');
    
    if (spinner) spinner.style.display = show ? 'block' : 'none';
    if (grid) grid.style.display = show ? 'none' : 'block';
}

// Show error message
function showError(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-danger success-message';
    alert.innerHTML = `
        <i class="fas fa-exclamation-triangle me-2"></i>${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;
    
    document.body.appendChild(alert);
    
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

// Show success message
function showSuccess(message) {
    const alert = document.createElement('div');
    alert.className = 'alert alert-success success-message';
    alert.innerHTML = `
        <i class="fas fa-check-circle me-2"></i>${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;
    
    document.body.appendChild(alert);
    
    setTimeout(() => {
        alert.remove();
    }, 3000);
}

// Scroll to products section
function scrollToProducts() {
    const productsSection = document.getElementById('productsSection');
    if (productsSection) {
        productsSection.scrollIntoView({ behavior: 'smooth' });
    }
}

// View product details
function viewProduct(productId) {
    // For now, just show an alert. In a real app, you'd navigate to a product detail page
    alert(`Ver detalles del producto ${productId}`);
}

// Check authentication status
function checkAuthStatus() {
    const token = localStorage.getItem('token');
    if (token) {
        // Verify token with backend
        verifyToken(token);
    } else {
        updateAuthUI(false);
    }
}

// Verify token with backend
async function verifyToken(token) {
    try {
        const response = await fetch(`${API_BASE_URL}/diagnostico/auth`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (response.ok) {
            const data = await response.json();
            if (data.authenticated) {
                currentUser = data.user;
                updateAuthUI(true);
            } else {
                localStorage.removeItem('token');
                updateAuthUI(false);
            }
        } else {
            localStorage.removeItem('token');
            updateAuthUI(false);
        }
    } catch (error) {
        console.error('Error verifying token:', error);
        localStorage.removeItem('token');
        updateAuthUI(false);
    }
}

// Update authentication UI
function updateAuthUI(isAuthenticated) {
    const authButtons = document.getElementById('authButtons');
    const userMenu = document.getElementById('userMenu');
    const adminPanel = document.getElementById('adminPanel');
    const userName = document.getElementById('userName');
    
    if (isAuthenticated && currentUser) {
        // User is logged in
        if (authButtons) authButtons.style.display = 'none';
        if (userMenu) {
            userMenu.style.display = 'block';
            if (userName) userName.textContent = currentUser.nombre || currentUser.email;
        }
        
        // Show admin panel if user is admin
        if (currentUser.role === 'ROLE_ADMIN' && adminPanel) {
            adminPanel.style.display = 'block';
        }
    } else {
        // User is not logged in
        if (authButtons) authButtons.style.display = 'block';
        if (userMenu) userMenu.style.display = 'none';
        if (adminPanel) adminPanel.style.display = 'none';
    }
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    currentUser = null;
    updateAuthUI(false);
    showSuccess('Sesión cerrada exitosamente');
    
    // Reload page to reset state
    setTimeout(() => {
        window.location.reload();
    }, 1000);
}

// Export functions for use in other modules
window.app = {
    API_BASE_URL,
    currentUser,
    showError,
    showSuccess,
    checkAuthStatus,
    updateAuthUI
}; 