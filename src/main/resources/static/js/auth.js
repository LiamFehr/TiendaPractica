// Authentication Module for Tienda Online

// Initialize authentication when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    setupAuthEventListeners();
});

// Set up authentication event listeners
function setupAuthEventListeners() {
    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
}

// Handle login form submission
async function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');
    const submitBtn = loginForm.querySelector('button[type="submit"]');
    
    // Validate inputs
    if (!email || !password) {
        showAuthError('Por favor completa todos los campos', errorDiv);
        return;
    }
    
    // Show loading state
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Iniciando Sesión...';
    hideAuthError(errorDiv);
    
    try {
        const response = await fetch(`${app.API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Login successful
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            
            // Update global user state
            window.currentUser = data.user;
            
            // Update UI
            app.updateAuthUI(true);
            
            // Show success message
            app.showSuccess('¡Bienvenido! Has iniciado sesión exitosamente');
            
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
            if (modal) {
                modal.hide();
            }
            
            // Reset form
            loginForm.reset();
            
            // Reload page to update all components
            setTimeout(() => {
                window.location.reload();
            }, 1000);
            
        } else {
            // Login failed
            const errorMessage = data.message || 'Error al iniciar sesión';
            showAuthError(errorMessage, errorDiv);
        }
        
    } catch (error) {
        console.error('Login error:', error);
        showAuthError('Error de conexión. Intenta nuevamente.', errorDiv);
    } finally {
        // Reset button state
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>Iniciar Sesión';
    }
}

// Handle register form submission
async function handleRegister(e) {
    e.preventDefault();
    
    const name = document.getElementById('registerName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    const errorDiv = document.getElementById('registerError');
    const submitBtn = registerForm.querySelector('button[type="submit"]');
    
    // Validate inputs
    if (!name || !email || !password) {
        showAuthError('Por favor completa todos los campos', errorDiv);
        return;
    }
    
    if (password.length < 6) {
        showAuthError('La contraseña debe tener al menos 6 caracteres', errorDiv);
        return;
    }
    
    // Show loading state
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Registrando...';
    hideAuthError(errorDiv);
    
    try {
        const response = await fetch(`${app.API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombre: name,
                email: email,
                password: password
            })
        });
        
        const data = await response.json();
        
        if (response.ok) {
            // Registration successful
            app.showSuccess('¡Registro exitoso! Ya puedes iniciar sesión');
            
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'));
            if (modal) {
                modal.hide();
            }
            
            // Reset form
            registerForm.reset();
            
            // Switch to login modal
            setTimeout(() => {
                const loginModal = new bootstrap.Modal(document.getElementById('loginModal'));
                loginModal.show();
            }, 1000);
            
        } else {
            // Registration failed
            const errorMessage = data.message || 'Error al registrarse';
            showAuthError(errorMessage, errorDiv);
        }
        
    } catch (error) {
        console.error('Registration error:', error);
        showAuthError('Error de conexión. Intenta nuevamente.', errorDiv);
    } finally {
        // Reset button state
        submitBtn.disabled = false;
        submitBtn.innerHTML = '<i class="fas fa-user-plus me-2"></i>Registrarse';
    }
}

// Show authentication error
function showAuthError(message, errorDiv) {
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
}

// Hide authentication error
function hideAuthError(errorDiv) {
    if (errorDiv) {
        errorDiv.style.display = 'none';
    }
}

// Get current user from localStorage
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (userStr) {
        try {
            return JSON.parse(userStr);
        } catch (error) {
            console.error('Error parsing user data:', error);
            return null;
        }
    }
    return null;
}

// Get authentication token
function getAuthToken() {
    return localStorage.getItem('token');
}

// Check if user is authenticated
function isAuthenticated() {
    const token = getAuthToken();
    const user = getCurrentUser();
    return token && user;
}

// Check if user is admin
function isAdmin() {
    const user = getCurrentUser();
    return user && user.role === 'ROLE_ADMIN';
}

// Make authenticated API request
async function authenticatedRequest(url, options = {}) {
    const token = getAuthToken();
    
    if (!token) {
        throw new Error('No authentication token found');
    }
    
    const defaultOptions = {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };
    
    const finalOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    };
    
    const response = await fetch(url, finalOptions);
    
    // If token is expired or invalid, logout user
    if (response.status === 401) {
        logout();
        throw new Error('Sesión expirada. Por favor inicia sesión nuevamente.');
    }
    
    return response;
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.currentUser = null;
    
    // Update UI
    if (window.app && window.app.updateAuthUI) {
        window.app.updateAuthUI(false);
    }
    
    // Show success message
    if (window.app && window.app.showSuccess) {
        window.app.showSuccess('Sesión cerrada exitosamente');
    }
    
    // Reload page to reset all state
    setTimeout(() => {
        window.location.reload();
    }, 1000);
}

// Auto-logout on token expiration
function setupTokenExpirationCheck() {
    const token = getAuthToken();
    if (token) {
        try {
            // Decode JWT token to get expiration
            const payload = JSON.parse(atob(token.split('.')[1]));
            const expirationTime = payload.exp * 1000; // Convert to milliseconds
            const currentTime = Date.now();
            
            if (currentTime >= expirationTime) {
                // Token is expired
                logout();
            } else {
                // Set timeout to logout when token expires
                const timeUntilExpiration = expirationTime - currentTime;
                setTimeout(() => {
                    logout();
                }, timeUntilExpiration);
            }
        } catch (error) {
            console.error('Error checking token expiration:', error);
            // If we can't decode the token, logout to be safe
            logout();
        }
    }
}

// Initialize token expiration check
document.addEventListener('DOMContentLoaded', function() {
    setupTokenExpirationCheck();
});

// Export functions for use in other modules
window.auth = {
    getCurrentUser,
    getAuthToken,
    isAuthenticated,
    isAdmin,
    authenticatedRequest,
    logout,
    handleLogin,
    handleRegister
}; 