document.addEventListener('DOMContentLoaded', function() {
    initPaginationAjax();
});

function initPaginationAjax() {
    const paginationLinks = document.querySelectorAll('.pagination a');
    const articlesContainer = document.querySelector('.col-md-8 section');
    
    if (!articlesContainer || !paginationLinks.length) {
        return;
    }
    
    paginationLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const pageUrl = this.getAttribute('href');
            
            fetch(pageUrl)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error de red: ' + response.status);
                    }
                    return response.text();
                })
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const newArticles = doc.querySelector('.col-md-8 section');
                    const newPagination = doc.querySelector('.pagination');
                    
                    if (newArticles && articlesContainer) {
                        articlesContainer.innerHTML = newArticles.innerHTML;
                    }
                    
                    if (newPagination) {
                        document.querySelector('.pagination').innerHTML = newPagination.innerHTML;
                    }
                    
                    // Actualizar URL sin recargar la página
                    history.pushState(null, null, pageUrl);
                    
                    // Reinicializar los eventos de paginación
                    initPaginationAjax();
                })
                .catch(error => {
                    console.error('Error cargando la página:', error);
                });
        });
    });
}