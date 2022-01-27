package com.example.application.views.helloworld;

import com.example.application.views.MainLayout;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.collaborationengine.PresenceManager;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.util.Arrays;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HelloWorldView extends VerticalLayout {
	private final EventBus eventBus = new EventBus();

	public HelloWorldView() {
		createButtons("one", "two", "three");
	}

	private void createButtons(String... names) {
		Arrays.stream(names).forEach(name -> {
			Jodel layout = new Jodel(name);
			add(layout);
			eventBus.register(layout);
		});
	}

	public class Jodel extends HorizontalLayout {
		private final PresenceManager presenceManager;
		private final String name;
		private final Button button;

		public Jodel(String name) {
			this.name = name;
			button = new Button("Here", event -> eventBus.post(new Event(name)));

			AvatarGroup avatarGroup = new AvatarGroup();
			avatarGroup.setMaxItemsVisible(5);
			String s = String.valueOf(System.identityHashCode(HelloWorldView.this));
			UserInfo userInfo = new UserInfo(s, s, "jodel");
			presenceManager = new PresenceManager(avatarGroup, userInfo, name);
			presenceManager.setPresenceHandler(context -> {
				var user = context.getUser();
				var avatar = new AvatarGroup.AvatarGroupItem(user.getName(),
						user.getImage());
				avatarGroup.add(avatar);
				return () -> {
					avatarGroup.remove(avatar);
				};
			});
			add(button, avatarGroup);
			presenceManager.markAsPresent(name.equals("one"));
		}

		@Subscribe
		public void switchJodel(Event event) {
			boolean present = event.name.equals(name);
			presenceManager.markAsPresent(present);
			button.setEnabled(!present);
		}
	}

	public static final class Event {
		private final String name;

		private Event(String name) {
			this.name = name;
		}

	}
}
