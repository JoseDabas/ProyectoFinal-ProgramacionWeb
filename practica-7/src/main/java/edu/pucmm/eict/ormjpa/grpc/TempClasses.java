package edu.pucmm.eict.ormjpa.grpc;

import io.grpc.stub.StreamObserver;
import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;

// Clases temporales para compilación
public class TempClasses {
    // Clase para Empty con patrón Builder
    public static class Empty {
        // Constructor privado
        private Empty() {
        }

        // Método estático para crear un Builder
        public static Builder newBuilder() {
            return new Builder();
        }

        // Clase Builder interna para Empty
        public static class Builder {
            public Empty build() {
                return new Empty();
            }
        }
    }

    // Clase básica para EstudianteMessage
    public static class EstudianteMessage {
        private int matricula;
        private String nombre;

        public int getMatricula() {
            return matricula;
        }

        public String getNombre() {
            return nombre;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private EstudianteMessage message = new EstudianteMessage();

            public Builder setMatricula(int matricula) {
                message.matricula = matricula;
                return this;
            }

            public Builder setNombre(String nombre) {
                message.nombre = nombre;
                return this;
            }

            public EstudianteMessage build() {
                return message;
            }
        }
    }

    // Clase para MatriculaRequest
    public static class MatriculaRequest {
        private int matricula;

        public int getMatricula() {
            return matricula;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private MatriculaRequest request = new MatriculaRequest();

            public Builder setMatricula(int matricula) {
                request.matricula = matricula;
                return this;
            }

            public MatriculaRequest build() {
                return request;
            }
        }
    }

    // Clase para Resultado
    public static class Resultado {
        private boolean exito;
        private String mensaje;

        public boolean getExito() {
            return exito;
        }

        public String getMensaje() {
            return mensaje;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private Resultado resultado = new Resultado();

            public Builder setExito(boolean exito) {
                resultado.exito = exito;
                return this;
            }

            public Builder setMensaje(String mensaje) {
                resultado.mensaje = mensaje;
                return this;
            }

            public Resultado build() {
                return resultado;
            }
        }
    }

    // Clase para ListaEstudiantes
    public static class ListaEstudiantes {
        private java.util.List<EstudianteMessage> estudiantes = new java.util.ArrayList<>();

        public java.util.List<EstudianteMessage> getEstudiantesList() {
            return estudiantes;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private ListaEstudiantes lista = new ListaEstudiantes();

            public Builder addAllEstudiantes(java.util.List<EstudianteMessage> estudiantes) {
                lista.estudiantes.addAll(estudiantes);
                return this;
            }

            public ListaEstudiantes build() {
                return lista;
            }
        }
    }

    // Clase temporal para EstudianteServiceGrpc
    public static class EstudianteServiceGrpc {
        // Clase base de implementación del servicio
        public static abstract class EstudianteServiceImplBase implements BindableService {
            public void listarEstudiantes(Empty request, StreamObserver<ListaEstudiantes> responseObserver) {
                responseObserver.onError(io.grpc.Status.UNIMPLEMENTED.asRuntimeException());
            }

            public void consultarEstudiante(MatriculaRequest request,
                    StreamObserver<EstudianteMessage> responseObserver) {
                responseObserver.onError(io.grpc.Status.UNIMPLEMENTED.asRuntimeException());
            }

            public void crearEstudiante(EstudianteMessage request, StreamObserver<Resultado> responseObserver) {
                responseObserver.onError(io.grpc.Status.UNIMPLEMENTED.asRuntimeException());
            }

            public void borrarEstudiante(MatriculaRequest request, StreamObserver<Resultado> responseObserver) {
                responseObserver.onError(io.grpc.Status.UNIMPLEMENTED.asRuntimeException());
            }

            @Override
            public ServerServiceDefinition bindService() {
                return ServerServiceDefinition.builder("estudiante.EstudianteService").build();
            }
        }

        public static EstudianteServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
            return new EstudianteServiceBlockingStub();
        }

        public static class EstudianteServiceBlockingStub {
            public ListaEstudiantes listarEstudiantes(Empty request) {
                return new ListaEstudiantes();
            }

            public EstudianteMessage consultarEstudiante(MatriculaRequest request) {
                return new EstudianteMessage();
            }

            public Resultado crearEstudiante(EstudianteMessage request) {
                return new Resultado();
            }

            public Resultado borrarEstudiante(MatriculaRequest request) {
                return new Resultado();
            }
        }
    }
}