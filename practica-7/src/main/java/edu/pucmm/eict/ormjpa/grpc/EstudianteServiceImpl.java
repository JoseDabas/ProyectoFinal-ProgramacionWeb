package edu.pucmm.eict.ormjpa.grpc;

import edu.pucmm.eict.ormjpa.modelos.Estudiante;
import edu.pucmm.eict.ormjpa.servicios.EstudianteServices;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

// Importaciones de las clases temporales
import edu.pucmm.eict.ormjpa.grpc.TempClasses.Empty;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.EstudianteMessage;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.ListaEstudiantes;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.MatriculaRequest;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.Resultado;
import edu.pucmm.eict.ormjpa.grpc.TempClasses.EstudianteServiceGrpc;

import java.util.List;
import java.util.stream.Collectors;

// Hereda de la clase base generada por gRPC (ahora de nuestra clase temporal)
public class EstudianteServiceImpl extends EstudianteServiceGrpc.EstudianteServiceImplBase {

    // Instancia de tu servicio JPA existente
    private final EstudianteServices estudianteServices = EstudianteServices.getInstancia();

    @Override
    public void listarEstudiantes(Empty request, StreamObserver<ListaEstudiantes> responseObserver) {
        try {
            List<edu.pucmm.eict.ormjpa.modelos.Estudiante> listaJpa = estudianteServices.findAll();

            // Convertir la lista de entidades JPA a mensajes
            List<EstudianteMessage> listaProto = listaJpa.stream()
                    .map(this::convertirAJpaAMensaje)
                    .collect(Collectors.toList());

            // Construir la respuesta
            ListaEstudiantes respuesta = ListaEstudiantes.newBuilder()
                    .addAllEstudiantes(listaProto)
                    .build();

            // Enviar la respuesta
            responseObserver.onNext(respuesta);
            responseObserver.onCompleted();

        } catch (Exception e) {
            System.err.println("Error en listarEstudiantes: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno al listar estudiantes")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void consultarEstudiante(MatriculaRequest request, StreamObserver<EstudianteMessage> responseObserver) {
        try {
            Estudiante estudianteJpa = estudianteServices.find(request.getMatricula());

            if (estudianteJpa != null) {
                // Convertir la entidad JPA a mensaje
                EstudianteMessage respuesta = convertirAJpaAMensaje(estudianteJpa);
                responseObserver.onNext(respuesta);
                responseObserver.onCompleted();
            } else {
                // Estudiante no encontrado
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Estudiante con matrícula " + request.getMatricula() + " no encontrado.")
                        .asRuntimeException());
            }
        } catch (Exception e) {
            System.err.println("Error en consultarEstudiante: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno al consultar estudiante")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void crearEstudiante(EstudianteMessage request, StreamObserver<Resultado> responseObserver) {
        try {
            // Convertir mensaje a entidad JPA
            Estudiante nuevoEstudiante = convertirMensajeAJpa(request);

            // Intentar crear usando el servicio existente
            Estudiante creado = estudianteServices.crear(nuevoEstudiante);

            Resultado respuesta;
            if (creado != null) {
                respuesta = Resultado.newBuilder()
                        .setExito(true)
                        .setMensaje("Estudiante con matrícula " + creado.getMatricula() + " creado exitosamente.")
                        .build();
            } else {
                // Esto no debería pasar si crear lanza excepción en caso de error
                respuesta = Resultado.newBuilder()
                        .setExito(false)
                        .setMensaje("No se pudo crear el estudiante.")
                        .build();
            }
            responseObserver.onNext(respuesta);
            responseObserver.onCompleted();

        } catch (jakarta.persistence.EntityExistsException eee) {
            System.err.println("Error al crear estudiante (ya existe): " + eee.getMessage());
            responseObserver.onError(Status.ALREADY_EXISTS
                    .withDescription("Estudiante con matrícula " + request.getMatricula() + " ya existe.")
                    .withCause(eee)
                    .asRuntimeException());
        } catch (Exception e) {
            System.err.println("Error en crearEstudiante: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno al crear estudiante: " + e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void borrarEstudiante(MatriculaRequest request, StreamObserver<Resultado> responseObserver) {
        try {
            boolean eliminado = estudianteServices.eliminar(request.getMatricula());
            Resultado respuesta;
            if (eliminado) {
                respuesta = Resultado.newBuilder()
                        .setExito(true)
                        .setMensaje("Estudiante con matrícula " + request.getMatricula() + " eliminado exitosamente.")
                        .build();
            } else {
                // Podría significar que no se encontró para eliminar
                respuesta = Resultado.newBuilder()
                        .setExito(false)
                        .setMensaje("No se pudo eliminar el estudiante con matrícula " + request.getMatricula()
                                + ". Puede que no exista.")
                        .build();
            }
            responseObserver.onNext(respuesta);
            responseObserver.onCompleted();

        } catch (Exception e) {
            System.err.println("Error en borrarEstudiante: " + e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno al borrar estudiante")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    // --- Métodos de ayuda para conversión ---

    private EstudianteMessage convertirAJpaAMensaje(Estudiante estudianteJpa) {
        EstudianteMessage.Builder builder = EstudianteMessage.newBuilder();
        builder.setMatricula(estudianteJpa.getMatricula());
        if (estudianteJpa.getNombre() != null) {
            builder.setNombre(estudianteJpa.getNombre());
        }
        // Si necesitas manejar fechas, tendrías que adaptarlo
        return builder.build();
    }

    private Estudiante convertirMensajeAJpa(EstudianteMessage mensajeProto) {
        Estudiante estudianteJpa = new Estudiante();
        estudianteJpa.setMatricula(mensajeProto.getMatricula());
        estudianteJpa.setNombre(mensajeProto.getNombre());
        // Si necesitas manejar fechas, tendrías que adaptarlo
        return estudianteJpa;
    }
}