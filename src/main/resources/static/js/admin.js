// Admin Panel Module for Tienda Online

// Admin state
let currentTab = 'dashboard';
let products = [];
let categories = [];
let orders = [];
let users = [];

// Initialize admin panel when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Admin Panel - Inicializado');
    
    // Check if user is admin
    if (!auth.isAuthenticated() || !auth.isAdmin()) {
        window.location.href = '/';
        return;
    }
    
    // Initialize admin panel
    initializeAdminPanel();
    
    // Set up event listeners
    setupAdminEventListeners();
    
    // Load initial data
    loadDashboardData();
});

// Initialize admin panel
function initializeAdminPanel() {
    // Update user info
    const user = auth.getCurrentUser();
    if (user) {
        const userName = document.getElementById('userName');
        if (userName) {
            userName.textContent = user.nombre || user.email;
        }
        
        const userMenu = document.getElementById('userMenu');
        if (userMenu) {
            userMenu.style.display = 'block';
        }
    }
}

// Set up admin event listeners
function setupAdminEventListeners() {
    // Tab navigation
    const tabLinks = document.querySelectorAll('[data-tab]');
    tabLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const tabName = this.getAttribute('data-tab');
            switchTab(tabName);
        });
    });
    
    // Logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            auth.logout();
        });
    }
}

// Switch between tabs
function switchTab(tabName) {
    // Update active tab
    document.querySelectorAll('[data-tab]').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    
    // Hide all tab content
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // Show selected tab content
    document.getElementById(tabName).classList.add('active');
    
    currentTab = tabName;
    
    // Load data for the selected tab
    switch (tabName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'products':
            loadProducts();
            break;
        case 'categories':
            loadCategories();
            break;
        case 'invoicing':
            loadInvoicingData();
            break;
        case 'orders':
            loadOrders();
            break;
        case 'users':
            loadUsers();
            break;
    }
}

// Load dashboard data
async function loadDashboardData() {
    try {
        // Load statistics
        const statsResponse = await auth.authenticatedRequest(`${app.API_BASE_URL}/productos/estadisticas`);
        if (statsResponse.ok) {
            const stats = await statsResponse.json();
            updateDashboardStats(stats);
        }
        
        // Load recent activity (simulated)
        updateRecentActivity();
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
        app.showError('Error al cargar datos del dashboard');
    }
}

// Update dashboard statistics
function updateDashboardStats(stats) {
    const totalProducts = document.getElementById('totalProducts');
    const totalCategories = document.getElementById('totalCategories');
    const totalOrders = document.getElementById('totalOrders');
    const totalUsers = document.getElementById('totalUsers');
    
    if (totalProducts) totalProducts.textContent = stats.totalProductos || 0;
    if (totalCategories) totalCategories.textContent = stats.totalCategorias || 0;
    if (totalOrders) totalOrders.textContent = stats.totalPedidos || 0;
    if (totalUsers) totalUsers.textContent = stats.totalUsuarios || 0;
}

// Update recent activity
function updateRecentActivity() {
    const activityContainer = document.getElementById('recentActivity');
    if (!activityContainer) return;
    
    // Simulated activity data
    const activities = [
        { type: 'product', message: 'Nuevo producto agregado: iPhone 15', time: '2 minutos' },
        { type: 'order', message: 'Pedido #1234 completado', time: '15 minutos' },
        { type: 'user', message: 'Nuevo usuario registrado', time: '1 hora' },
        { type: 'category', message: 'Categoría "Tecnología" actualizada', time: '2 horas' }
    ];
    
    activityContainer.innerHTML = activities.map(activity => `
        <div class="activity-item d-flex align-items-center">
            <div class="activity-icon bg-primary text-white">
                <i class="fas fa-${getActivityIcon(activity.type)}"></i>
            </div>
            <div class="activity-content">
                <div class="fw-medium">${activity.message}</div>
                <div class="activity-time">${activity.time}</div>
            </div>
        </div>
    `).join('');
}

// Get activity icon
function getActivityIcon(type) {
    switch (type) {
        case 'product': return 'box';
        case 'order': return 'shopping-bag';
        case 'user': return 'user';
        case 'category': return 'tags';
        default: return 'info-circle';
    }
}

// Load products
async function loadProducts() {
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/productos`);
        if (response.ok) {
            products = await response.json();
            displayProductsTable(products);
        }
    } catch (error) {
        console.error('Error loading products:', error);
        app.showError('Error al cargar productos');
    }
}

// Display products table
function displayProductsTable(productsToShow) {
    const tbody = document.getElementById('productsTableBody');
    if (!tbody) return;
    
    if (!productsToShow || productsToShow.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No hay productos</td></tr>';
        return;
    }
    
    tbody.innerHTML = productsToShow.map(product => `
        <tr>
            <td>${product.id}</td>
            <td>
                <img src="${product.imagenUrl || 'https://via.placeholder.com/50x50?text=Sin+Imagen'}" 
                     alt="${product.nombre}" class="product-image">
            </td>
            <td>${product.nombre}</td>
            <td>${product.categoria || 'Sin categoría'}</td>
            <td>$${product.precio.toFixed(2)}</td>
            <td>${product.stock}</td>
            <td>
                <span class="status-badge ${product.activo ? 'status-active' : 'status-inactive'}">
                    ${product.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <div class="product-actions">
                    <button class="btn btn-sm btn-outline-primary" onclick="editProduct(${product.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteProduct(${product.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Load categories
async function loadCategories() {
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/categorias`);
        if (response.ok) {
            categories = await response.json();
            displayCategoriesTable(categories);
            populateCategorySelects(categories);
        }
    } catch (error) {
        console.error('Error loading categories:', error);
        app.showError('Error al cargar categorías');
    }
}

// Display categories table
function displayCategoriesTable(categoriesToShow) {
    const tbody = document.getElementById('categoriesTableBody');
    if (!tbody) return;
    
    if (!categoriesToShow || categoriesToShow.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No hay categorías</td></tr>';
        return;
    }
    
    tbody.innerHTML = categoriesToShow.map(category => `
        <tr>
            <td>${category.id}</td>
            <td>${category.nombre}</td>
            <td>${category.descripcion || 'Sin descripción'}</td>
            <td>
                <span class="category-color" style="background-color: ${category.color}"></span>
            </td>
            <td>
                <span class="category-order">${category.orden}</span>
            </td>
            <td>
                <span class="status-badge ${category.activo ? 'status-active' : 'status-inactive'}">
                    ${category.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <div class="product-actions">
                    <button class="btn btn-sm btn-outline-primary" onclick="editCategory(${category.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteCategory(${category.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Populate category selects
function populateCategorySelects(categories) {
    const selects = ['productCategory', 'searchCategory'];
    
    selects.forEach(selectId => {
        const select = document.getElementById(selectId);
        if (select) {
            // Keep the first option
            const firstOption = select.querySelector('option');
            select.innerHTML = '';
            if (firstOption) {
                select.appendChild(firstOption);
            }
            
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.nombre;
                select.appendChild(option);
            });
        }
    });
}

// Load invoicing data
async function loadInvoicingData() {
    try {
        // Load categories for search
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/categorias`);
        if (response.ok) {
            const categories = await response.json();
            populateCategorySelects(categories);
        }
    } catch (error) {
        console.error('Error loading invoicing data:', error);
    }
}

// Search invoicing products
async function searchInvoicingProducts() {
    const code = document.getElementById('searchCode').value;
    const name = document.getElementById('searchName').value;
    const categoryId = document.getElementById('searchCategory').value;
    
    if (!code && !name && !categoryId) {
        app.showError('Por favor ingrese al menos un criterio de búsqueda');
        return;
    }
    
    try {
        const searchData = {};
        if (code) searchData.codigoInterno = code;
        if (name) searchData.nombre = name;
        if (categoryId) searchData.categoriaId = categoryId;
        
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/facturador/buscar`, {
            method: 'POST',
            body: JSON.stringify(searchData)
        });
        
        if (response.ok) {
            const results = await response.json();
            displayInvoicingResults(results);
        }
    } catch (error) {
        console.error('Error searching products:', error);
        app.showError('Error al buscar productos');
    }
}

// Display invoicing results
function displayInvoicingResults(results) {
    const container = document.getElementById('invoicingResults');
    if (!container) return;
    
    if (!results || results.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-search"></i>
                <h3>No se encontraron productos</h3>
                <p>Intenta con otros criterios de búsqueda.</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = `
        <div class="results-grid">
            ${results.map(product => `
                <div class="result-card">
                    <div class="d-flex align-items-center mb-3">
                        <img src="${product.imagenUrl || 'https://via.placeholder.com/60x60?text=Sin+Imagen'}" 
                             alt="${product.nombre}" class="product-image me-3">
                        <div>
                            <h6 class="mb-1">${product.nombre}</h6>
                            <small class="text-muted">Código: ${product.codigoInterno}</small>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-6">
                            <strong>Precio:</strong><br>
                            $${product.precio.toFixed(2)}
                        </div>
                        <div class="col-6">
                            <strong>Stock:</strong><br>
                            ${product.stock}
                        </div>
                    </div>
                    <div class="mt-3">
                        <strong>Categoría:</strong> ${product.categoria || 'Sin categoría'}
                    </div>
                </div>
            `).join('')}
        </div>
    `;
}

// Clear invoicing search
function clearInvoicingSearch() {
    document.getElementById('searchCode').value = '';
    document.getElementById('searchName').value = '';
    document.getElementById('searchCategory').value = '';
    
    const container = document.getElementById('invoicingResults');
    if (container) {
        container.innerHTML = '<p class="text-muted">Realice una búsqueda para ver los productos.</p>';
    }
}

// Load orders
async function loadOrders() {
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/pedidos`);
        if (response.ok) {
            orders = await response.json();
            displayOrdersTable(orders);
        }
    } catch (error) {
        console.error('Error loading orders:', error);
        app.showError('Error al cargar pedidos');
    }
}

// Display orders table
function displayOrdersTable(ordersToShow) {
    const tbody = document.getElementById('ordersTableBody');
    if (!tbody) return;
    
    if (!ordersToShow || ordersToShow.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay pedidos</td></tr>';
        return;
    }
    
    tbody.innerHTML = ordersToShow.map(order => `
        <tr>
            <td>${order.id}</td>
            <td>${order.usuario?.nombre || 'N/A'}</td>
            <td>$${order.total.toFixed(2)}</td>
            <td>
                <span class="status-badge ${order.estado === 'COMPLETADO' ? 'status-active' : 'status-inactive'}">
                    ${order.estado}
                </span>
            </td>
            <td>${new Date(order.fechaCreacion).toLocaleDateString()}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="viewOrder(${order.id})">
                    <i class="fas fa-eye"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Load users
async function loadUsers() {
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/usuarios`);
        if (response.ok) {
            users = await response.json();
            displayUsersTable(users);
        }
    } catch (error) {
        console.error('Error loading users:', error);
        app.showError('Error al cargar usuarios');
    }
}

// Display users table
function displayUsersTable(usersToShow) {
    const tbody = document.getElementById('usersTableBody');
    if (!tbody) return;
    
    if (!usersToShow || usersToShow.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No hay usuarios</td></tr>';
        return;
    }
    
    tbody.innerHTML = usersToShow.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>
                <div class="d-flex align-items-center">
                    <div class="user-avatar me-2">
                        ${user.nombre ? user.nombre.charAt(0).toUpperCase() : 'U'}
                    </div>
                    ${user.nombre}
                </div>
            </td>
            <td>${user.email}</td>
            <td>
                <span class="user-role ${user.role === 'ROLE_ADMIN' ? 'admin' : 'user'}">
                    ${user.role === 'ROLE_ADMIN' ? 'Admin' : 'Usuario'}
                </span>
            </td>
            <td>
                <span class="status-badge status-active">Activo</span>
            </td>
            <td>
                <div class="product-actions">
                    <button class="btn btn-sm btn-outline-primary" onclick="editUser(${user.id})">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${user.id})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Show add product modal
function showAddProductModal() {
    const modal = new bootstrap.Modal(document.getElementById('addProductModal'));
    modal.show();
}

// Save product
async function saveProduct() {
    const formData = {
        nombre: document.getElementById('productName').value,
        descripcion: document.getElementById('productDescription').value,
        precio: parseFloat(document.getElementById('productPrice').value),
        stock: parseInt(document.getElementById('productStock').value),
        categoriaId: document.getElementById('productCategory').value,
        codigoInterno: document.getElementById('productCode').value,
        imagenUrl: document.getElementById('productImage').value
    };
    
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/productos`, {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            app.showSuccess('Producto guardado exitosamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('addProductModal'));
            modal.hide();
            loadProducts();
        } else {
            throw new Error('Error al guardar producto');
        }
    } catch (error) {
        console.error('Error saving product:', error);
        app.showError('Error al guardar producto');
    }
}

// Show add category modal
function showAddCategoryModal() {
    const modal = new bootstrap.Modal(document.getElementById('addCategoryModal'));
    modal.show();
}

// Save category
async function saveCategory() {
    const formData = {
        nombre: document.getElementById('categoryName').value,
        descripcion: document.getElementById('categoryDescription').value,
        color: document.getElementById('categoryColor').value,
        orden: parseInt(document.getElementById('categoryOrder').value)
    };
    
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/categorias`, {
            method: 'POST',
            body: JSON.stringify(formData)
        });
        
        if (response.ok) {
            app.showSuccess('Categoría guardada exitosamente');
            const modal = bootstrap.Modal.getInstance(document.getElementById('addCategoryModal'));
            modal.hide();
            loadCategories();
        } else {
            throw new Error('Error al guardar categoría');
        }
    } catch (error) {
        console.error('Error saving category:', error);
        app.showError('Error al guardar categoría');
    }
}

// Edit product (placeholder)
function editProduct(productId) {
    app.showError('Función de edición no implementada aún');
}

// Delete product
async function deleteProduct(productId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este producto?')) {
        return;
    }
    
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/productos/${productId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            app.showSuccess('Producto eliminado exitosamente');
            loadProducts();
        } else {
            throw new Error('Error al eliminar producto');
        }
    } catch (error) {
        console.error('Error deleting product:', error);
        app.showError('Error al eliminar producto');
    }
}

// Edit category (placeholder)
function editCategory(categoryId) {
    app.showError('Función de edición no implementada aún');
}

// Delete category
async function deleteCategory(categoryId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta categoría?')) {
        return;
    }
    
    try {
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/categorias/${categoryId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            app.showSuccess('Categoría eliminada exitosamente');
            loadCategories();
        } else {
            throw new Error('Error al eliminar categoría');
        }
    } catch (error) {
        console.error('Error deleting category:', error);
        app.showError('Error al eliminar categoría');
    }
}

// View order (placeholder)
function viewOrder(orderId) {
    app.showError('Función de visualización no implementada aún');
}

// Edit user (placeholder)
function editUser(userId) {
    app.showError('Función de edición no implementada aún');
}

// Delete user (placeholder)
function deleteUser(userId) {
    app.showError('Función de eliminación no implementada aún');
}

// Export functions for use in other modules
window.admin = {
    switchTab,
    loadProducts,
    loadCategories,
    searchInvoicingProducts,
    clearInvoicingSearch,
    showAddProductModal,
    saveProduct,
    showAddCategoryModal,
    saveCategory
}; 