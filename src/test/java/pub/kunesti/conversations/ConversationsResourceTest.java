package pub.kunesti.conversations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import pub.kunesti.config.BaseIT;


public class ConversationsResourceTest extends BaseIT {

    @Test
    @Sql({"/data/eventData.sql", "/data/conversationsData.sql"})
    void getAllConversationss_success() throws Exception {
        mockMvc.perform(get("/api/conversationss")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1200)));
    }

    @Test
    @Sql({"/data/eventData.sql", "/data/conversationsData.sql"})
    void getAllConversationss_filtered() throws Exception {
        mockMvc.perform(get("/api/conversationss?filter=1201")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(((long)1201)));
    }

    @Test
    @Sql({"/data/eventData.sql", "/data/conversationsData.sql"})
    void getConversations_success() throws Exception {
        mockMvc.perform(get("/api/conversationss/1200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat."));
    }

    @Test
    void getConversations_notFound() throws Exception {
        mockMvc.perform(get("/api/conversationss/1866")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    @Sql("/data/eventData.sql")
    void createConversations_success() throws Exception {
        mockMvc.perform(post("/api/conversationss")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/conversationsDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, conversationsRepository.count());
    }

    @Test
    void createConversations_missingField() throws Exception {
        mockMvc.perform(post("/api/conversationss")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/conversationsDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("message"));
    }

    @Test
    @Sql({"/data/eventData.sql", "/data/conversationsData.sql"})
    void updateConversations_success() throws Exception {
        mockMvc.perform(put("/api/conversationss/1200")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/conversationsDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.", conversationsRepository.findById(((long)1200)).get().getMessage());
        assertEquals(2, conversationsRepository.count());
    }

    @Test
    @Sql({"/data/eventData.sql", "/data/conversationsData.sql"})
    void deleteConversations_success() throws Exception {
        mockMvc.perform(delete("/api/conversationss/1200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, conversationsRepository.count());
    }

}
