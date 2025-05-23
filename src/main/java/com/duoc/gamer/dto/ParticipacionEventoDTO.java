package com.duoc.gamer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipacionEventoDTO {

    @NotNull(message = "El campo id no puede estar vacío")
    @Positive(message = "El campo id debe ser un número positivo")
    private Long idParticipacionEvento;

    @NotNull(message = "El campo usuario no puede estar vacío")
    private Long idUsuario;

    @NotNull(message = "El campo evento no puede estar vacío")
    private Long idEvento;

    @NotNull(message = "El campo fechaInscripcion no puede estar vacío")
    private LocalDate fechaInscripcion;
}
