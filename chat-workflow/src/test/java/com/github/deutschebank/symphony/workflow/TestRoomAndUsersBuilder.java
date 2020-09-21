package com.github.deutschebank.symphony.workflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig;
import com.github.deutschebank.symphony.workflow.room.Rooms;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRoomsImpl;
import com.symphony.api.model.RoomSystemInfo;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V2RoomAttributes;
import com.symphony.api.model.V2RoomDetail;
import com.symphony.api.model.V2UserList;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {  TestWorkflowConfig.class })
public class TestRoomAndUsersBuilder {
	
	Rooms ruBuilder;
	
	@MockBean
	RoomMembershipApi rmApi;
	
	@MockBean
	StreamsApi streamsApi;
	
	@MockBean
	UsersApi usersApi;
	
	@Autowired
	Workflow wf;
	
	@Before
	public void setup() {
		
		// just returns a single real user
		when(usersApi.v3UsersGet(any(), any(), any(), any(), any()))
			.then(a -> new V2UserList().users(Collections.singletonList(new UserV2().id(45l).displayName("Robert Moffat").emailAddress("rob@kite9.com"))));
		
		when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
		.then(a -> new UserV2().id(45l).displayName("Robert Moffat").emailAddress("rob@kite9.com"));
	
		
		// one room
		when(streamsApi.v1StreamsListPost(any(), any(), any(), any()))
			.then(a -> {
				StreamList out = new StreamList();
				out.add(new StreamAttributes().id("123"));
				return out;
			});
		
		when(streamsApi.v2RoomIdInfoGet(eq("123"), any()))
			.then(a -> new V2RoomDetail().roomAttributes(new V2RoomAttributes()._public(true).name("Initial Room").description("Bogus")));
		
		when(streamsApi.v3RoomCreatePost(any(), isNull()))
			.then(a -> new V3RoomDetail()
					.roomSystemInfo(new RoomSystemInfo().id("456"))
					.roomAttributes(new V3RoomAttributes()._public(false).name("Some Test Room").description("Still Bogus")));
		
		when(streamsApi.v3RoomIdInfoGet(any(), any()))
			.then(a -> new V3RoomDetail().roomAttributes(new V3RoomAttributes().name("Some Room").description("Bogus Room")._public(true)));
		
		ruBuilder = new SymphonyRoomsImpl(wf, rmApi, streamsApi, usersApi);
		
	}
	
	@Test
	public void testCreateRoom() {
		RoomDef rd = new RoomDef("Some Test Room", "Automated Test Room Created", true, null);
		Room out = ruBuilder.ensureRoom(rd);
		assertEquals("Some Test Room", out.getRoomName());
		assertEquals(1, ruBuilder.getAllRooms().size());
	}
}