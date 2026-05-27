// scripts.js - JavaScript para Pelukeria

// Validación del formulario de registro
document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.querySelector('form[action="/register"]');
    
    if (registerForm) {
        registerForm.addEventListener('submit', function(event) {
            const password = document.getElementById('pasahitza');
            const confirmPassword = document.getElementById('confirmPassword');
            
            if (password && confirmPassword && password.value !== confirmPassword.value) {
                event.preventDefault();
                alert('Las contraseñas no coinciden');
                confirmPassword.focus();
            }
        });
    }
    
    // Tooltips de Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    console.log('Pelukeria - JavaScript cargado correctamente');
});