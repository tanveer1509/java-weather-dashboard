package app;                                                                                                        
                                                                                                                    
import javax.swing.*;                                                                                               
import java.awt.*;                                                                                                  
import java.awt.event.ActionEvent;                                                                                  
import java.awt.event.ActionListener;                                                                               
import java.io.BufferedReader;                                                                                      
import java.io.InputStreamReader;                                                                                   
import java.net.HttpURLConnection;                                                                                  
import java.net.URL;                                                                                                
import java.text.SimpleDateFormat;                                                                                  
import java.util.Date;                                                                                              
                                                                                                                    
public class WeatherApp {                                                                                           
                                                                                                                    
	// MY API KEY                                                                                                   
	private static final String API_KEY = "3f5d2ac8e825ab9ef22b4c0e9d92d802";                                       
                                                                                                                    
	// UI components                                                                                                
	private JFrame frame;                                                                                           
	private JTextField cityInput;                                                                                   
	private JLabel weatherIcon, tempLabel, conditionLabel, humidityLabel, windLabel;                                
	private JTextArea historyArea;                                                                                  
	private JComboBox<String> unitBox;                                                                              
	private JPanel mainPanel;                                                                                       
                                                                                                                    
	public static void main(String[] args) {                                                                        
		// running the gui safely                                                                                   
		SwingUtilities.invokeLater(() -> {                                                                          
			try {                                                                                                   
				WeatherApp window = new WeatherApp();                                                               
				window.frame.setVisible(true);                                                                      
			} catch (Exception e) {                                                                                 
				e.printStackTrace();                                                                                
			}                                                                                                       
		});                                                                                                         
	}                                                                                                               
                                                                                                                    
	public WeatherApp() {                                                                                           
		initialize();                                                                                               
	}                                                                                                               
                                                                                                                    
	private void initialize() {                                                                                     
		// setting up the main window                                                                               
		frame = new JFrame();                                                                                       
		frame.setTitle("Weather App");                                                                              
		frame.setBounds(100, 100, 500, 600);                                                                        
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                                       
		frame.getContentPane().setLayout(new BorderLayout(0, 0));                                                   
                                                                                                                    
		// top panel for search and options                                                                         
		JPanel topPanel = new JPanel();                                                                             
		topPanel.setBackground(new Color(240, 248, 255));                                                           
		frame.getContentPane().add(topPanel, BorderLayout.NORTH);                                                   
                                                                                                                    
		JLabel lblCity = new JLabel("City:");                                                                       
		topPanel.add(lblCity);                                                                                      
                                                                                                                    
		cityInput = new JTextField();                                                                               
		cityInput.setColumns(10);                                                                                   
		topPanel.add(cityInput);                                                                                    
                                                                                                                    
		// dropdown for units                                                                                       
		String[] units = { "Celsius", "Fahrenheit" };                                                               
		unitBox = new JComboBox<>(units);                                                                           
		topPanel.add(unitBox);                                                                                      
                                                                                                                    
		JButton btnSearch = new JButton("Get Weather");                                                             
		// adding click listener to the button                                                                      
		btnSearch.addActionListener(new ActionListener() {                                                          
			public void actionPerformed(ActionEvent e) {                                                            
				getWeatherData();                                                                                   
			}                                                                                                       
		});                                                                                                         
		topPanel.add(btnSearch);                                                                                    
                                                                                                                    
		// center panel to show results                                                                             
		mainPanel = new JPanel();                                                                                   
		mainPanel.setLayout(new GridLayout(5, 1));                                                                  
		mainPanel.setBackground(Color.WHITE);                                                                       
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);                                                 
                                                                                                                    
		weatherIcon = new JLabel("", SwingConstants.CENTER);                                                        
		mainPanel.add(weatherIcon);                                                                                 
                                                                                                                    
		tempLabel = new JLabel("Temp: --", SwingConstants.CENTER);                                                  
		tempLabel.setFont(new Font("Arial", Font.BOLD, 24));                                                        
		mainPanel.add(tempLabel);                                                                                   
                                                                                                                    
		conditionLabel = new JLabel("Condition: --", SwingConstants.CENTER);                                        
		mainPanel.add(conditionLabel);                                                                              
                                                                                                                    
		humidityLabel = new JLabel("Humidity: --", SwingConstants.CENTER);                                          
		mainPanel.add(humidityLabel);                                                                               
                                                                                                                    
		windLabel = new JLabel("Wind: --", SwingConstants.CENTER);                                                  
		mainPanel.add(windLabel);                                                                                   
                                                                                                                    
		// bottom panel for search history                                                                          
		JPanel bottomPanel = new JPanel();                                                                          
		bottomPanel.setLayout(new BorderLayout());                                                                  
		bottomPanel.setBorder(BorderFactory.createTitledBorder("History"));                                         
                                                                                                                    
		historyArea = new JTextArea(5, 20);                                                                         
		historyArea.setEditable(false);                                                                             
		JScrollPane scrollPane = new JScrollPane(historyArea);                                                      
		bottomPanel.add(scrollPane, BorderLayout.CENTER);                                                           
                                                                                                                    
		frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);                                                
	}                                                                                                               
                                                                                                                    
	private void getWeatherData() {                                                                                 
		String city = cityInput.getText().trim();                                                                   
		if (city.isEmpty()) {                                                                                       
			JOptionPane.showMessageDialog(frame, "Please enter a city.");                                           
			return;                                                                                                 
		}                                                                                                           
                                                                                                                    
		try {                                                                                                       
			// check if user wants metric or imperial                                                               
			String selectedUnit = (String) unitBox.getSelectedItem();                                               
			String unitParam = selectedUnit.equals("Celsius") ? "metric" : "imperial";                              
			String symbol = selectedUnit.equals("Celsius") ? "°C" : "°F";                                           
                                                                                                                    
			// building the api url                                                                                 
			String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY    
					+ "&units=" + unitParam;                                                                        
                                                                                                                    
			URL url = new URL(urlString);                                                                           
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();                                      
			conn.setRequestMethod("GET");                                                                          
                                                                                                                    
			int responseCode = conn.getResponseCode();                                                              
			if (responseCode == 200) {                                                                              
				// reading the response                                                                             
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));               
				String line;                                                                                        
				StringBuilder response = new StringBuilder();                                                       
				while ((line = in.readLine()) != null) {                                                            
					response.append(line);                                                                          
				}                                                                                                   
				in.close();                                                                                         
                                                                                                                    
				updateUI(response.toString(), city, symbol);                                                        
                                                                                                                    
			} else {                                                                                                
				JOptionPane.showMessageDialog(frame, "City not found.");                                            
			}                                                                                                       
                                                                                                                    
		} catch (Exception e) {                                                                                     
			e.printStackTrace();                                                                                    
			JOptionPane.showMessageDialog(frame, "Connection failed.");                                             
		}                                                                                                           
	}                                                                                                               
                                                                                                                    
	private void updateUI(String json, String city, String symbol) {                                                
		// parsing json manually to avoid extra libraries                                                           
		String temp = findValue(json, "\"temp\":");                                                                 
		String humidity = findValue(json, "\"humidity\":");                                                         
		String speed = findValue(json, "\"speed\":");                                                               
		String desc = findString(json, "\"description\":");                                                         
		String iconCode = findString(json, "\"icon\":");                                                            
                                                                                                                    
		// updating labels                                                                                          
		tempLabel.setText("Temp: " + temp + symbol);                                                                
		humidityLabel.setText("Humidity: " + humidity + "%");                                                       
		windLabel.setText("Wind: " + speed);                                                                        
		conditionLabel.setText("Condition: " + desc.toUpperCase());                                                 
                                                                                                                    
		// trying to load the icon image                                                                            
		try {                                                                                                       
			String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";                            
			weatherIcon.setIcon(new ImageIcon(new URL(iconUrl)));                                                   
		} catch (Exception e) {                                                                                     
			weatherIcon.setText("No Icon");                                                                         
		}                                                                                                           
                                                                                                                    
		// changing background if it's night time                                                                   
		if (iconCode.contains("n")) {                                                                               
			mainPanel.setBackground(new Color(25, 25, 112)); // dark blue                                           
			tempLabel.setForeground(Color.WHITE);                                                                   
			conditionLabel.setForeground(Color.WHITE);                                                              
		} else {                                                                                                    
			mainPanel.setBackground(new Color(135, 206, 235)); // light blue                                        
			tempLabel.setForeground(Color.BLACK);                                                                   
			conditionLabel.setForeground(Color.BLACK);                                                              
		}                                                                                                           
                                                                                                                    
		// adding to history                                                                                        
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());                                          
		historyArea.append("[" + time + "] " + city + ": " + temp + symbol + "\n");                                 
	}                                                                                                               
                                                                                                                    
	// simple helper to find numbers in json                                                                        
	private String findValue(String json, String key) {                                                             
		int start = json.indexOf(key) + key.length();                                                               
		int end = json.indexOf(",", start);                                                                         
		if (end == -1)                                                                                              
			end = json.indexOf("}", start);                                                                         
		return json.substring(start, end);                                                                          
	}                                                                                                               
                                                                                                                    
	// simple helper to find text in json                                                                           
	private String findString(String json, String key) {                                                            
		int start = json.indexOf(key) + key.length() + 1;                                                           
		int end = json.indexOf("\"", start);                                                                        
		return json.substring(start, end);                                                                          
	}                                                                                                               
}      
