package br.com.devgui.banktxtapi.controller.response;

public record ErrorResponseDTO (int status,
                             String error,
                             String message) {
}
