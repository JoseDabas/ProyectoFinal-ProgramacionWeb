// URL base de la API
const API_URL = 'http://localhost:7000/api/estudiante/';

// Elementos del DOM que se cargarán cuando el documento esté listo
let cuerpoTabla, formBuscar, resultadoBusqueda, formCrear, resultadoCrear, formEliminar, resultadoEliminar;

// Inicialización de elementos cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', () => {
    // Capturar referencias a elementos del DOM
    cuerpoTabla = document.getElementById('cuerpoTabla');
    formBuscar = document.getElementById('formBuscar');
    resultadoBusqueda = document.getElementById('resultadoBusqueda');
    formCrear = document.getElementById('formCrear');
    resultadoCrear = document.getElementById('resultadoCrear');
    formEliminar = document.getElementById('formEliminar');
    resultadoEliminar = document.getElementById('resultadoEliminar');
    
    // Configurar event listeners
    formBuscar.addEventListener('submit', function(e) {
        e.preventDefault();
        const matricula = document.getElementById('buscarMatricula').value;
        consultarEstudiante(matricula);
    });
    
    formCrear.addEventListener('submit', function(e) {
        e.preventDefault();
        const estudiante = {
            matricula: parseInt(document.getElementById('crearMatricula').value),
            nombre: document.getElementById('crearNombre').value
        };
        crearEstudiante(estudiante);
    });
    
    formEliminar.addEventListener('submit', function(e) {
        e.preventDefault();
        const matricula = document.getElementById('eliminarMatricula').value;
        confirmarEliminar(matricula);
    });
    
    // Cargar estudiantes inicialmente
    listarEstudiantes();
});

// Función para listar todos los estudiantes
async function listarEstudiantes() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }
        
        const estudiantes = await response.json();
        cuerpoTabla.innerHTML = '';
        
        estudiantes.forEach(estudiante => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${estudiante.matricula}</td>
                <td>${estudiante.nombre}</td>
                <td>
                    <button class="btn-danger" onclick="confirmarEliminar(${estudiante.matricula})">Eliminar</button>
                </td>
            `;
            cuerpoTabla.appendChild(row);
        });
    } catch (error) {
        console.error('Error al listar estudiantes:', error);
        alert('Error al listar estudiantes: ' + error.message);
    }
}

// Función para consultar un estudiante
async function consultarEstudiante(matricula) {
    try {
        const response = await fetch(`${API_URL}${matricula}`);
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }
        
        const estudiante = await response.json();
        resultadoBusqueda.innerHTML = `
            <h3>Estudiante encontrado:</h3>
            <p>Matrícula: ${estudiante.matricula}</p>
            <p>Nombre: ${estudiante.nombre}</p>
        `;
        resultadoBusqueda.className = 'status success';
        
        // Llenar el formulario de búsqueda para futuras consultas
        document.getElementById('buscarMatricula').value = estudiante.matricula;
    } catch (error) {
        console.error('Error al consultar estudiante:', error);
        resultadoBusqueda.innerHTML = 'Error al consultar estudiante: ' + error.message;
        resultadoBusqueda.className = 'status error';
    }
}

// Función para crear un estudiante
async function crearEstudiante(estudiante) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(estudiante)
        });
        
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }
        
        const estudianteCreado = await response.json();
        resultadoCrear.innerHTML = `
            <h3>Estudiante creado con éxito:</h3>
            <p>Matrícula: ${estudianteCreado.matricula}</p>
            <p>Nombre: ${estudianteCreado.nombre}</p>
        `;
        resultadoCrear.className = 'status success';
        
        // Actualizar la lista de estudiantes
        listarEstudiantes();
        
        // Limpiar el formulario
        formCrear.reset();
    } catch (error) {
        console.error('Error al crear estudiante:', error);
        resultadoCrear.innerHTML = 'Error al crear estudiante: ' + error.message;
        resultadoCrear.className = 'status error';
    }
}

// Función para eliminar un estudiante
async function eliminarEstudiante(matricula) {
    try {
        const response = await fetch(`${API_URL}${matricula}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }
        
        const resultado = await response.json();
        if (resultado) {
            resultadoEliminar.innerHTML = `Estudiante con matrícula ${matricula} eliminado correctamente.`;
            resultadoEliminar.className = 'status success';
            
            // Actualizar la lista de estudiantes
            listarEstudiantes();
            
            // Limpiar el formulario
            formEliminar.reset();
        } else {
            resultadoEliminar.innerHTML = `No se pudo eliminar el estudiante con matrícula ${matricula}.`;
            resultadoEliminar.className = 'status error';
        }
    } catch (error) {
        console.error('Error al eliminar estudiante:', error);
        resultadoEliminar.innerHTML = 'Error al eliminar estudiante: ' + error.message;
        resultadoEliminar.className = 'status error';
    }
}

// Función para confirmar eliminación
function confirmarEliminar(matricula) {
    if (confirm(`¿Está seguro que desea eliminar el estudiante con matrícula ${matricula}?`)) {
        eliminarEstudiante(matricula);
    }
}