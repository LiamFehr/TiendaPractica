// Shopping Cart Module for Tienda Online

// Cart state
let cartItems = [];
let cartTotal = 0;

// Initialize cart when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    loadCartFromStorage();
    updateCartBadge();
});

// Load cart from localStorage
function loadCartFromStorage() {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
        try {
            cartItems = JSON.parse(savedCart);
            calculateCartTotal();
        } catch (error) {
            console.error('Error loading cart from storage:', error);
            cartItems = [];
        }
    }
}

// Save cart to localStorage
function saveCartToStorage() {
    localStorage.setItem('cart', JSON.stringify(cartItems));
}

// Add product to cart
async function addToCart(productId) {
    try {
        // Check if user is authenticated
        if (!auth.isAuthenticated()) {
            app.showError('Debes iniciar sesión para agregar productos al carrito');
            return;
        }
        
        // Get product details
        const product = await getProductById(productId);
        if (!product) {
            app.showError('Producto no encontrado');
            return;
        }
        
        // Check if product is already in cart
        const existingItem = cartItems.find(item => item.productoId === productId);
        
        if (existingItem) {
            // Update quantity if stock allows
            if (existingItem.cantidad < product.stock) {
                existingItem.cantidad += 1;
                app.showSuccess('Cantidad actualizada en el carrito');
            } else {
                app.showError('No hay más stock disponible');
                return;
            }
        } else {
            // Add new item to cart
            const newItem = {
                productoId: productId,
                nombre: product.nombre,
                precio: product.precio,
                imagenUrl: product.imagenUrl,
                cantidad: 1,
                stock: product.stock
            };
            cartItems.push(newItem);
            app.showSuccess('Producto agregado al carrito');
        }
        
        // Update cart
        calculateCartTotal();
        saveCartToStorage();
        updateCartBadge();
        
        // Show cart modal
        showCart();
        
    } catch (error) {
        console.error('Error adding to cart:', error);
        app.showError('Error al agregar producto al carrito');
    }
}

// Get product by ID
async function getProductById(productId) {
    try {
        const response = await fetch(`${app.API_BASE_URL}/productos/${productId}`);
        if (response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Error fetching product:', error);
        return null;
    }
}

// Update cart item quantity
async function updateCartItemQuantity(productId, newQuantity) {
    const item = cartItems.find(item => item.productoId === productId);
    if (!item) return;
    
    if (newQuantity <= 0) {
        // Remove item from cart
        removeFromCart(productId);
        return;
    }
    
    if (newQuantity > item.stock) {
        app.showError('No hay suficiente stock disponible');
        return;
    }
    
    item.cantidad = newQuantity;
    calculateCartTotal();
    saveCartToStorage();
    updateCartBadge();
    displayCartItems();
}

// Remove item from cart
function removeFromCart(productId) {
    cartItems = cartItems.filter(item => item.productoId !== productId);
    calculateCartTotal();
    saveCartToStorage();
    updateCartBadge();
    displayCartItems();
    app.showSuccess('Producto removido del carrito');
}

// Calculate cart total
function calculateCartTotal() {
    cartTotal = cartItems.reduce((total, item) => {
        return total + (item.precio * item.cantidad);
    }, 0);
}

// Update cart badge
function updateCartBadge() {
    const badge = document.getElementById('cartBadge');
    if (badge) {
        const totalItems = cartItems.reduce((total, item) => total + item.cantidad, 0);
        badge.textContent = totalItems;
        badge.style.display = totalItems > 0 ? 'block' : 'none';
    }
}

// Show cart modal
function showCart() {
    displayCartItems();
    const cartModal = new bootstrap.Modal(document.getElementById('cartModal'));
    cartModal.show();
}

// Display cart items
function displayCartItems() {
    const cartItemsContainer = document.getElementById('cartItems');
    const cartTotalElement = document.getElementById('cartTotal');
    
    if (!cartItemsContainer) return;
    
    if (cartItems.length === 0) {
        cartItemsContainer.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-shopping-cart"></i>
                <h3>Tu carrito está vacío</h3>
                <p>Agrega algunos productos para comenzar a comprar.</p>
            </div>
        `;
        if (cartTotalElement) cartTotalElement.textContent = '0.00';
        return;
    }
    
    cartItemsContainer.innerHTML = cartItems.map(item => `
        <div class="cart-item">
            <div class="row align-items-center">
                <div class="col-md-2">
                    <img src="${item.imagenUrl || 'https://via.placeholder.com/80x80?text=Sin+Imagen'}" 
                         alt="${item.nombre}" class="img-fluid">
                </div>
                <div class="col-md-4">
                    <h6 class="mb-1">${item.nombre}</h6>
                    <p class="text-muted mb-0">Stock: ${item.stock}</p>
                </div>
                <div class="col-md-2">
                    <span class="price">$${item.precio.toFixed(2)}</span>
                </div>
                <div class="col-md-2">
                    <div class="quantity-controls">
                        <button class="quantity-btn" onclick="updateCartItemQuantity(${item.productoId}, ${item.cantidad - 1})">
                            <i class="fas fa-minus"></i>
                        </button>
                        <span class="mx-2 fw-bold">${item.cantidad}</span>
                        <button class="quantity-btn" onclick="updateCartItemQuantity(${item.productoId}, ${item.cantidad + 1})">
                            <i class="fas fa-plus"></i>
                        </button>
                    </div>
                </div>
                <div class="col-md-1">
                    <span class="fw-bold">$${(item.precio * item.cantidad).toFixed(2)}</span>
                </div>
                <div class="col-md-1">
                    <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart(${item.productoId})">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    if (cartTotalElement) {
        cartTotalElement.textContent = cartTotal.toFixed(2);
    }
}

// Clear cart
function limpiarCarrito() {
    if (cartItems.length === 0) {
        app.showError('El carrito ya está vacío');
        return;
    }
    
    if (confirm('¿Estás seguro de que quieres vaciar el carrito?')) {
        cartItems = [];
        calculateCartTotal();
        saveCartToStorage();
        updateCartBadge();
        displayCartItems();
        app.showSuccess('Carrito vaciado');
    }
}

// Proceed to checkout
async function procederCheckout() {
    if (!auth.isAuthenticated()) {
        app.showError('Debes iniciar sesión para proceder al checkout');
        return;
    }
    
    if (cartItems.length === 0) {
        app.showError('El carrito está vacío');
        return;
    }
    
    try {
        // Create order
        const orderData = {
            items: cartItems.map(item => ({
                productoId: item.productoId,
                cantidad: item.cantidad,
                precioUnitario: item.precio
            })),
            total: cartTotal
        };
        
        const response = await auth.authenticatedRequest(`${app.API_BASE_URL}/carrito/checkout`, {
            method: 'POST',
            body: JSON.stringify(orderData)
        });
        
        if (response.ok) {
            const order = await response.json();
            
            // Create payment
            const paymentResponse = await auth.authenticatedRequest(`${app.API_BASE_URL}/pagos/crear`, {
                method: 'POST',
                body: JSON.stringify({
                    pedidoId: order.id,
                    monto: cartTotal,
                    metodoPago: 'MERCADO_PAGO'
                })
            });
            
            if (paymentResponse.ok) {
                const payment = await paymentResponse.json();
                
                // Clear cart
                cartItems = [];
                calculateCartTotal();
                saveCartToStorage();
                updateCartBadge();
                
                // Close cart modal
                const cartModal = bootstrap.Modal.getInstance(document.getElementById('cartModal'));
                if (cartModal) {
                    cartModal.hide();
                }
                
                // Show success message
                app.showSuccess('Pedido creado exitosamente. Redirigiendo al pago...');
                
                // Redirect to payment page (in a real app, this would be Mercado Pago)
                setTimeout(() => {
                    alert(`En una aplicación real, aquí se redirigiría a Mercado Pago para completar el pago.\n\nPedido ID: ${order.id}\nTotal: $${cartTotal.toFixed(2)}`);
                }, 2000);
                
            } else {
                throw new Error('Error al crear el pago');
            }
            
        } else {
            throw new Error('Error al crear el pedido');
        }
        
    } catch (error) {
        console.error('Checkout error:', error);
        app.showError('Error al procesar el checkout: ' + error.message);
    }
}

// Initialize cart
function initializeCart() {
    loadCartFromStorage();
    updateCartBadge();
}

// Export functions for use in other modules
window.cart = {
    addToCart,
    removeFromCart,
    updateCartItemQuantity,
    limpiarCarrito,
    procederCheckout,
    showCart,
    getCartItems: () => cartItems,
    getCartTotal: () => cartTotal
}; 