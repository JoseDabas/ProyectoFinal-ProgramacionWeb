package edu.pucmm.eict.ormjpa.grpc.client;

// Importa las clases temporales
import edu.pucmm.eict.ormjpa.grpc.TempClasses.Empty;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.EstudianteMessage;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.ListaEstudiantes;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.MatriculaRequest;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.Resultado;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.EstudianteServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClienteGrpcEstudiante {

    private final ManagedChannel channel;
    private final EstudianteServiceGrpc.EstudianteServiceBlockingStub blockingStub; // Stub síncrono

    // Constructor
    public ClienteGrpcEstudiante(String host, int port) {
        // Crear el canal de comunicación
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                // Usar plaintext para desarrollo. En producción usa TLS.
                .usePlaintext()
                .build();

        // Crear el stub para realizar llamadas
        this.blockingStub = EstudianteServiceGrpc.newBlockingStub(channel);
        System.out.println("Cliente gRPC conectado a " + host + ":" + port);
    }

    // Método para apagar el canal
    public void shutdown() throws InterruptedException {
        System.out.println("Apagando canal del cliente gRPC...");
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Canal apagado.");
    }

    // --- Métodos para interactuar con el servicio ---

    public void listarTodos() {
        System.out.println("\n--- Listando todos los estudiantes ---");
        Empty request = Empty.newBuilder().build();
        try {
            ListaEstudiantes response = blockingStub.listarEstudiantes(request);
            List<EstudianteMessage> lista = response.getEstudiantesList();
            if (lista.isEmpty()) {
                System.out.println("No se encontraron estudiantes.");
            } else {
                lista.forEach(est -> System.out
                        .println("  Matrícula: " + est.getMatricula() + ", Nombre: " + est.getNombre()));
            }
        } catch (StatusRuntimeException e) {
            System.err.println("RPC falló (Listar): " + e.getStatus());
        }
    }

    public void consultar(int matricula) {
        System.out.println("\n--- Consultando estudiante con matrícula: " + matricula + " ---");
        MatriculaRequest request = MatriculaRequest.newBuilder().setMatricula(matricula).build();
        try {
            EstudianteMessage response = blockingStub.consultarEstudiante(request);
            System.out.println(
                    "  Encontrado: Matrícula: " + response.getMatricula() + ", Nombre: " + response.getNombre());
        } catch (StatusRuntimeException e) {
            System.err.println("RPC falló (Consultar " + matricula + "): " + e.getStatus());
        }
    }

    public void crear(int matricula, String nombre) {
        System.out.println("\n--- Creando estudiante: Matrícula=" + matricula + ", Nombre=" + nombre + " ---");
        EstudianteMessage request = EstudianteMessage.newBuilder()
                .setMatricula(matricula)
                .setNombre(nombre)
                .build();
        try {
            Resultado response = blockingStub.crearEstudiante(request);
            System.out.println(
                    "  Resultado Creación: " + response.getMensaje() + " (Éxito: " + response.getExito() + ")");
        } catch (StatusRuntimeException e) {
            System.err.println("RPC falló (Crear " + matricula + "): " + e.getStatus());
        }
    }

    public void borrar(int matricula) {
        System.out.println("\n--- Borrando estudiante con matrícula: " + matricula + " ---");
        MatriculaRequest request = MatriculaRequest.newBuilder().setMatricula(matricula).build();
        try {
            Resultado response = blockingStub.borrarEstudiante(request);
            System.out
                    .println("  Resultado Borrado: " + response.getMensaje() + " (Éxito: " + response.getExito() + ")");
        } catch (StatusRuntimeException e) {
            System.err.println("RPC falló (Borrar " + matricula + "): " + e.getStatus());
        }
    }

    // --- Main para probar el cliente ---
    public static void main(String[] args) {
        ClienteGrpcEstudiante cliente = new ClienteGrpcEstudiante("localhost", 9090); // Usa el puerto gRPC

        try {
            // Probar las operaciones
            cliente.listarTodos();

            cliente.crear(2025001, "Nuevo Estudiante gRPC");
            cliente.crear(2025002, "Otro Estudiante gRPC");

            cliente.listarTodos();

            cliente.consultar(2025001);
            cliente.consultar(9999999); // Probar uno que no existe

            cliente.borrar(2025002);
            cliente.borrar(8888888); // Probar borrar uno que no existe

            cliente.listarTodos();

            // Intentar crear uno que ya existe (si no fue borrado)
            cliente.crear(2025001, "Intentando Duplicar");

        } catch (Exception e) {
            System.err.println("Error inesperado en el cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                cliente.shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error al apagar el cliente.");
                e.printStackTrace();
            }
        }
    }
}