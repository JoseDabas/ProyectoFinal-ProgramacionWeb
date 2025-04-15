package edu.pucmm.eict.ormjpa.controladores;

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
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Controlador que actúa como puente entre REST y gRPC
 * Permite que el cliente web se comunique con el servidor gRPC
 */
public class EstudianteGrpcControlador {

    private static final String GRPC_HOST = "localhost";
    private static final int GRPC_PORT = 9090;

    /**
     * Obtiene un canal de comunicación con el servidor gRPC
     */
    private static ManagedChannel getChannel() {
        return ManagedChannelBuilder.forAddress(GRPC_HOST, GRPC_PORT)
                .usePlaintext()
                .build();
    }

    /**
     * Lista todos los estudiantes desde el servicio gRPC
     */
    public static void listarEstudiantes(@NotNull Context ctx) {
        ManagedChannel channel = getChannel();
        try {
            EstudianteServiceGrpc.EstudianteServiceBlockingStub stub = EstudianteServiceGrpc.newBlockingStub(channel);

            // Llamada al servicio gRPC
            ListaEstudiantes response = stub.listarEstudiantes(Empty.newBuilder().build());

            // Devolver la respuesta como JSON
            ctx.json(response);
        } catch (StatusRuntimeException e) {
            ctx.status(500).json(Map.of("error", "Error gRPC: " + e.getStatus()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        } finally {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Consulta un estudiante por su matrícula
     */
    public static void consultarEstudiante(@NotNull Context ctx) {
        int matricula = ctx.pathParamAsClass("matricula", Integer.class).get();
        ManagedChannel channel = getChannel();

        try {
            EstudianteServiceGrpc.EstudianteServiceBlockingStub stub = EstudianteServiceGrpc.newBlockingStub(channel);

            // Construir la solicitud gRPC
            MatriculaRequest request = MatriculaRequest.newBuilder()
                    .setMatricula(matricula)
                    .build();

            // Llamada al servicio gRPC
            EstudianteMessage response = stub.consultarEstudiante(request);

            // Devolver la respuesta como JSON
            ctx.json(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().name().equals("NOT_FOUND")) {
                ctx.status(404).json(Map.of("error", "Estudiante no encontrado"));
            } else {
                ctx.status(500).json(Map.of("error", "Error gRPC: " + e.getStatus()));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        } finally {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Crea un nuevo estudiante a través del servicio gRPC
     */
    public static void crearEstudiante(@NotNull Context ctx) {
        ManagedChannel channel = getChannel();
        try {
            // Obtener los datos del estudiante de la solicitud JSON
            Map<String, Object> requestData = ctx.bodyAsClass(Map.class);
            int matricula = ((Number) requestData.get("matricula")).intValue();
            String nombre = (String) requestData.get("nombre");

            EstudianteServiceGrpc.EstudianteServiceBlockingStub stub = EstudianteServiceGrpc.newBlockingStub(channel);

            // Construir el mensaje Estudiante para gRPC
            EstudianteMessage request = EstudianteMessage.newBuilder()
                    .setMatricula(matricula)
                    .setNombre(nombre)
                    .build();

            // Llamada al servicio gRPC
            Resultado response = stub.crearEstudiante(request);

            // Devolver la respuesta
            ctx.json(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().name().equals("ALREADY_EXISTS")) {
                ctx.status(409).json(Map.of(
                        "exito", false,
                        "mensaje", "El estudiante con esta matrícula ya existe"));
            } else {
                ctx.status(500).json(Map.of(
                        "exito", false,
                        "mensaje", "Error gRPC: " + e.getStatus()));
            }
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                    "exito", false,
                    "mensaje", e.getMessage()));
        } finally {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Elimina un estudiante por su matrícula
     */
    public static void eliminarEstudiante(@NotNull Context ctx) {
        int matricula = ctx.pathParamAsClass("matricula", Integer.class).get();
        ManagedChannel channel = getChannel();

        try {
            EstudianteServiceGrpc.EstudianteServiceBlockingStub stub = EstudianteServiceGrpc.newBlockingStub(channel);

            // Construir la solicitud gRPC
            MatriculaRequest request = MatriculaRequest.newBuilder()
                    .setMatricula(matricula)
                    .build();

            // Llamada al servicio gRPC
            Resultado response = stub.borrarEstudiante(request);

            // Devolver la respuesta como JSON
            ctx.json(response);
        } catch (StatusRuntimeException e) {
            ctx.status(500).json(Map.of(
                    "exito", false,
                    "mensaje", "Error gRPC: " + e.getStatus()));
        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "exito", false,
                    "mensaje", e.getMessage()));
        } finally {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}