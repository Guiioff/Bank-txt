package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransacaoController.class)
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransacaoService transacaoService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public TransacaoService transacaoService() {
            return mock(TransacaoService.class);
        }
    }

    @Test
    void deveFazerUpload() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "teste.txt", "text/plain",
                "2024-06-01;DEPOSITO;100.00\n".getBytes());

        doNothing().when(transacaoService).processarTransacoes(any(), anyLong());

        mockMvc.perform(multipart("/transacoes/upload")
                        .file(arquivo)
                        .param("usuarioId", "1"))
                .andExpect(status().isCreated());

        verify(transacaoService).processarTransacoes(any(), eq(1L));
    }
}
