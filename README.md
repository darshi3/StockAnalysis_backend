
# 📊 StockAnalysis Backend

This is the backend service for the **StockAnalysis** application, built with Java and Spring Boot. It integrates with the [Alpha Vantage API](https://www.alphavantage.co/) 
to fetch historical stock data and utilizes the [OpenAI API](https://platform.openai.com/) to generate AI-driven analyses of selected Indian stocks.

##  Features

- **Stock Selection**: Users can select any Indian stock and specify the number of past days for analysis.
- **Data Retrieval**: Fetches historical stock data (typically up to the previous day) from Alpha Vantage based on user input.
- **AI-Powered Analysis**: Sends the retrieved data to OpenAI's GPT model to generate insightful stock analyses.
- **API Communication**: Designed to work seamlessly with the Angular frontend via RESTful APIs.

##  Technologies Used

- **Java 21**
- **Spring Boot 3.x**
- **RESTful APIs**
- **Alpha Vantage API** for Historical stock Data
- **OpenAI GPT API** for generating analyses

### Prerequisites

- **Java 21** installed
- **Maven** installed
- **Alpha Vantage API Key**: Obtain from [Alpha Vantage](https://www.alphavantage.co/support/#api-key)
- **OpenAI API Key**: Obtain from [OpenAI](https://platform.openai.com/account/api-keys)

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/darshi3/StockAnalysis_backend.git
   cd StockAnalysis_backend
   ```

2. **Configure API Keys**:

   Create a file named `application.properties` in the `src/main/resources` directory and add your API keys:

   ```properties
   alpha.vantage.api.key=YOUR_ALPHA_VANTAGE_API_KEY
   openai.api.key=YOUR_OPENAI_API_KEY
   ```

3. **Build and Run the Application**:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend service will start on `http://localhost:8082`.

##  API Endpoints

- **POST `/api/analyze`**: Accepts a JSON payload with the stock symbol and number of days, returns the AI-generated analysis.

   **Request Body**:

   ```json
   {
     "symbol": "RELIANCE.BSE",
     "days": 6
   }
   ```

   **Response**:

   ```json
   {
     "analysis": "Based on the past 6 days, Reliance Industries has shown..."
   }
   ```



