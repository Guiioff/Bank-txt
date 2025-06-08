package br.com.devgui.banktxtapi.controller;

import br.com.devgui.banktxtapi.controller.response.UsuarioResumoResponseDTO;
import br.com.devgui.banktxtapi.controller.response.UsuarioTransacoesResponseDTO;
import br.com.devgui.banktxtapi.model.enums.TipoTransacao;
import br.com.devgui.banktxtapi.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@Import(UsuarioControllerTest.Config.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @TestConfiguration
    static class Config {
        @Bean
        public UsuarioService usuarioService() {
            return mock(UsuarioService.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(usuarioService);
    }

    @Test
    void deveCadastrarUsuario() throws Exception {
        String json = """
                {
                  "nome": "Joao",
                  "email": "joao@email.com",
                  "senha": "123456"
                }
                """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(usuarioService).cadastrarUsuario(any());
    }

    @Test
    void deveBuscarTransacoes() throws Exception {
        UsuarioTransacoesResponseDTO dto = new UsuarioTransacoesResponseDTO("Joao", "joao@email.com", List.of());
        when(usuarioService.listarTransacoes(1L)).thenReturn(dto);

        mockMvc.perform(get("/usuarios/1/transacoes"))
                .andExpect(status().isOk());

        verify(usuarioService).listarTransacoes(1L);
    }

    @Test
    void deveBuscarTransacoesPorData() throws Exception {
        UsuarioTransacoesResponseDTO dto = new UsuarioTransacoesResponseDTO("Joao", "joao@email.com", List.of());
        when(usuarioService.listarTransacoesEntreDatas(eq(1L), any(), any())).thenReturn(dto);

        mockMvc.perform(get("/usuarios/1/transacoes/por-data")
                        .param("inicio", "2024-05-01")
                        .param("fim", "2024-06-01"))
                .andExpect(status().isOk());

        verify(usuarioService).listarTransacoesEntreDatas(eq(1L), any(), any());
    }

    @Test
    void deveBuscarResumoDoUsuario() throws Exception {
        UsuarioResumoResponseDTO dto = new UsuarioResumoResponseDTO(
                1L,
                "Joao",
                Map.of("inicio", LocalDate.now(), "fim", LocalDate.now()),
                2,
                Map.of(TipoTransacao.DEPOSITO, 1),
                Map.of(TipoTransacao.DEPOSITO, BigDecimal.TEN),
                BigDecimal.TEN,
                BigDecimal.TEN,
                LocalDate.now()
        );

        when(usuarioService.obterResumo(1L)).thenReturn(dto);

        mockMvc.perform(get("/usuarios/1/resumo"))
                .andExpect(status().isOk());

        verify(usuarioService).obterResumo(1L);
    }
}
