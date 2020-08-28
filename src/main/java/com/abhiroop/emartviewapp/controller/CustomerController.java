package com.abhiroop.emartviewapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.abhiroop.emartviewapp.model.Customer;
import com.abhiroop.emartviewapp.model.ResponseObject;

@RestController
public class CustomerController {

	@Autowired
	private RestTemplate restTemplate;

	private String emartUserServiceUrl;

	@Value("${service.emart.url}")
	public void setServiceShoppingUrl(String url) {
		setEmartUserServiceUrl(url);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllCustomers", method = RequestMethod.GET, headers = "Accept=application/json")
	public List<Customer> getAllCustomers(Model model) {
		List<Customer> listOfCustomers = new ArrayList<Customer>();
		try {
			listOfCustomers = restTemplate.getForObject(getEmartUserServiceUrl() + "getAllUsers/", List.class);
		} catch (RestClientException e) {
			model.addAttribute("ERR", "No Connectivity with Back-end System");
			System.out.println("Exception occurred at getAllCustomers =>" + e.getMessage());
		}

		model.addAttribute("customer", new Customer());
		model.addAttribute("listOfCustomers", listOfCustomers);
		return listOfCustomers;
	}

	@RequestMapping(value = "/userServiceBackendConnectivity", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseObject userServiceBackendConnectivity() {
		ResponseObject r = new ResponseObject();
		String serviceStatus = "DOWN";
		String connectivityStatus = "DOWN";
		try {
			connectivityStatus = restTemplate.getForObject(getEmartUserServiceUrl(), String.class);
			if ("UserData-service App is ready".equals(connectivityStatus)) {
				serviceStatus = "UP";
			}
		} catch (RestClientException e) {
			System.out.println("Exception occurred at userServiceBackendConnectivity =>" + e.getMessage());
		}
		r.setMessage(connectivityStatus);
		return r;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
	public String goToHomePage() {
		return "use context /userdata.html";
	}

	@RequestMapping(value = "/getCustomer/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public Customer getCustomerById(@PathVariable int id) {
		Customer c = null;
		try {
			c = restTemplate.getForObject(getEmartUserServiceUrl() + "getUser/" + id, Customer.class);
		} catch (RestClientException e) {
			System.out.println("Exception occurred at getCustomerById =>" + e.getMessage());
		}

		return c;
	}

	@RequestMapping(value = "/addCustomer", method = RequestMethod.POST, headers = "Accept=application/json")
	public Customer addCustomer(@RequestBody Customer c) {

		String url = getEmartUserServiceUrl() + "addUser/";
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Customer> request = new HttpEntity<>(c, headers);
			ResponseEntity<Customer> response = restTemplate.exchange(url, HttpMethod.POST, request,
					new ParameterizedTypeReference<Customer>() {
					});
			c = response.getBody();
		} catch (RestClientException e) {
			System.out.println("Exception occurred at getCustomerById =>" + e.getMessage());
		}
		return c;

	}

	@RequestMapping(value = "/addCustomer", method = RequestMethod.PUT, headers = "Accept=application/json")
	public Customer updateCustomer(@RequestBody Customer customer) {
		customer = addCustomer(customer);
		System.out.println("Customer updated");
		return customer;

	}

	@RequestMapping(value = "/deleteCustomer/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseObject deleteCustomer(@PathVariable("id") int id) {
		ResponseObject r = new ResponseObject();
		String result = "FAIL";
		String uri = getEmartUserServiceUrl() + "deleteUser/" + id;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<Customer> entity = new HttpEntity<Customer>(headers);
		result = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class).getBody();
		r.setMessage(result);
		System.out.println(result);
		return r;
	}

	public String getEmartUserServiceUrl() {
		return emartUserServiceUrl;
	}

	public void setEmartUserServiceUrl(String emartUserServiceUrl) {
		this.emartUserServiceUrl = emartUserServiceUrl;
	}

}
