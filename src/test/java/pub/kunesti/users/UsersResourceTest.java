package pub.kunesti.users;

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


public class UsersResourceTest extends BaseIT {

    @Test
    @Sql("/data/usersData.sql")
    void getAllUserss_success() throws Exception {
        mockMvc.perform(get("/api/userss")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1100)));
    }

    @Test
    @Sql("/data/usersData.sql")
    void getAllUserss_filtered() throws Exception {
        mockMvc.perform(get("/api/userss?filter=1101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(((long)1101)));
    }

    @Test
    @Sql("/data/usersData.sql")
    void getUsers_success() throws Exception {
        mockMvc.perform(get("/api/userss/1100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("Duis autem vel."));
    }

    @Test
    void getUsers_notFound() throws Exception {
        mockMvc.perform(get("/api/userss/1766")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createUsers_success() throws Exception {
        mockMvc.perform(post("/api/userss")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/usersDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, usersRepository.count());
    }

    @Test
    void createUsers_missingField() throws Exception {
        mockMvc.perform(post("/api/userss")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/usersDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("userName"));
    }

    @Test
    @Sql("/data/usersData.sql")
    void updateUsers_success() throws Exception {
        mockMvc.perform(put("/api/userss/1100")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/usersDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Nam liber tempor.", usersRepository.findById(((long)1100)).get().getUserName());
        assertEquals(2, usersRepository.count());
    }

    @Test
    @Sql("/data/usersData.sql")
    void deleteUsers_success() throws Exception {
        mockMvc.perform(delete("/api/userss/1100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, usersRepository.count());
    }

}
