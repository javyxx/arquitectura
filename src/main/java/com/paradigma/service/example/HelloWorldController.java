package com.paradigma.service.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.paradigma.arquitecture.command.Commander;
import com.paradigma.arquitecture.command.EventCommand;
import com.paradigma.arquitecture.event.EventBus;

@RestController
@RequestMapping("/api/v1")
public class HelloWorldController {

	@Autowired
	private EventBus eventBus;

	@Autowired
	private Commander commander;

	@RequestMapping(value = "/hi", method = RequestMethod.GET)
	@PreAuthorize("@permissions.allowAny('hello', 'read')")
	public String helloWorld() {
		return "Hello World!";
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@PreAuthorize("@permissions.allowAny('model', 'read')")
	@PostFilter("@permissions.allow(filterObject.id, 'model', 'read')")
	public List<Model> findAll() {
		List<Model> models = new ArrayList<>();
		Model model = new Model("1", "Javier");
		models.add(model);
		Model model2 = new Model("2", "Otro");
		models.add(model2);
		Model model3 = new Model("5", "Denny");
		models.add(model3);

		return models;
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	@PreAuthorize("@permissions.allow(#id, 'model', 'read')")
	public Model findById(@PathVariable("id") String id) {
		return new Model(id, "Hi");
	}

	@RequestMapping(value = "/launchEvent", method = RequestMethod.GET)
	public void launchEvent() {
		commander.executeCommandOnEvent(PruebaEvent.class, new EventCommand<Object>() {

			@Override
			protected void execute() {
				// TODO Auto-generated method stub
				System.out.println(this.event);
			}
		});
		eventBus.publishEvent(new PruebaEvent());

	}
}
