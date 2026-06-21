package com.bencebanyai.kanban.kanbantaskmanager.web.controller;

import com.bencebanyai.kanban.kanbantaskmanager.security.BoardSecurity;
import com.bencebanyai.kanban.kanbantaskmanager.security.JwtAuthenticationFilter;
import com.bencebanyai.kanban.kanbantaskmanager.service.BoardService;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.BoardResponse;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.CreateBoardRequest;
import com.bencebanyai.kanban.kanbantaskmanager.web.dto.board.UpdateBoardRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BoardService boardService;

    @MockitoBean(name = "boardSecurity")
    private BoardSecurity boardSecurity;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(username = "test@example.com")
    void createBoard_ShouldReturnCreatedBoard() throws Exception {
        CreateBoardRequest request = new CreateBoardRequest("Test", "Desc");
        BoardResponse dto = new BoardResponse(1L, "Test", "Desc", Instant.now(), Instant.now(), false, 1L);

        when(boardService.createBoard(any(CreateBoardRequest.class), eq("test@example.com")))
                .thenReturn(dto);

        mockMvc
                .perform(
                        post("/api/boards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getUserBoards_ShouldReturnBoardsList() throws Exception {
        BoardResponse dto = new BoardResponse(1L, "Test", "Desc", Instant.now(), Instant.now(), false, 1L);
        when(boardService.getUserBoards("test@example.com")).thenReturn(List.of(dto));

        mockMvc
                .perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getBoardById_ShouldReturnBoard() throws Exception {
        BoardResponse dto = new BoardResponse(1L, "Test", "Desc", Instant.now(), Instant.now(), false, 1L);
        when(boardSecurity.isOwner(1L, "test@example.com")).thenReturn(true);
        when(boardService.getBoardById(1L)).thenReturn(dto);

        mockMvc
                .perform(get("/api/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void updateBoard_ShouldReturnUpdatedBoard() throws Exception {
        UpdateBoardRequest request = new UpdateBoardRequest("Updated", "Desc", false);
        BoardResponse dto = new BoardResponse(1L, "Updated", "Desc", Instant.now(), Instant.now(), false, 1L);

        when(boardSecurity.isOwner(1L, "test@example.com")).thenReturn(true);
        when(boardService.updateBoard(eq(1L), any(UpdateBoardRequest.class))).thenReturn(dto);

        mockMvc
                .perform(
                        put("/api/boards/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void archiveBoard_ShouldReturnNoContent() throws Exception {
        when(boardSecurity.isOwner(1L, "test@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/boards/1")).andExpect(status().isNoContent());

        verify(boardService).archiveBoard(1L);
    }
}
