document.addEventListener('DOMContentLoaded', function() {
    // Selector para el contenedor de la paginación
    const paginationContainer = document.querySelector('.pagination');
    
    if (paginationContainer) {
        // Agregar manejador de eventos para todos los enlaces de paginación
        paginationContainer.addEventListener('click', function(e) {
            // Verificar si se hizo clic en un enlace
            if (e.target.tagName === 'A') {
                e.preventDefault();
                
                // Obtener la URL del enlace
                const url = e.target.getAttribute('href');
                
                // Realizar solicitud AJAX
                fetch(url)
                    .then(response => response.text())
                    .then(html => {
                        // Crear un DOM temporal con el HTML recibido
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(html, 'text/html');
                        
                        // Reemplazar solo la sección de artículos
                        const articlesSection = doc.querySelector('.col-md-8');
                        if (articlesSection) {
                            document.querySelector('.col-md-8').innerHTML = articlesSection.innerHTML;
                        }
                        
                        // Actualizar la paginación
                        const newPagination = doc.querySelector('.pagination');
                        if (newPagination) {
                            paginationContainer.innerHTML = newPagination.innerHTML;
                        }
                        
                        // Actualizar la URL sin recargar la página
                        history.pushState({}, '', url);
                    })
                    .catch(error => {
                        console.error('Error en la paginación AJAX:', error);
                    });
            }
        });
    }
});