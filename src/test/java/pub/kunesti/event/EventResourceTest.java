package pub.kunesti.event;

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


public class EventResourceTest extends BaseIT {

    @Test
    @Sql("/data/eventData.sql")
    void getAllEvents_success() throws Exception {
        mockMvc.perform(get("/api/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1000)));
    }

    @Test
    @Sql("/data/eventData.sql")
    void getAllEvents_filtered() throws Exception {
        mockMvc.perform(get("/api/events?filter=1001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(((long)1001)));
    }

    @Test
    @Sql("/data/eventData.sql")
    void getEvent_success() throws Exception {
        mockMvc.perform(get("/api/events/1000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Lorem ipsum dolor."));
    }

    @Test
    void getEvent_notFound() throws Exception {
        mockMvc.perform(get("/api/events/1666")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createEvent_success() throws Exception {
        mockMvc.perform(post("/api/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/eventDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, eventRepository.count());
    }

    @Test
    void createEvent_missingField() throws Exception {
        mockMvc.perform(post("/api/events")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/eventDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("title"));
    }

    @Test
    @Sql("/data/eventData.sql")
    void updateEvent_success() throws Exception {
        mockMvc.perform(put("/api/events/1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/eventDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Ut wisi enim.", eventRepository.findById(((long)1000)).get().getTitle());
        assertEquals(2, eventRepository.count());
    }

    @Test
    @Sql("/data/eventData.sql")
    void deleteEvent_success() throws Exception {
        mockMvc.perform(delete("/api/events/1000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, eventRepository.count());
    }

}
