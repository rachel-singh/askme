package com.rachelsingh.askme;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;


public class CountryCapitalsSpeechlet implements Speechlet {
	
		private static final Logger log = LoggerFactory.getLogger(CountryCapitalsSpeechlet.class);
		
		private static final String SESSION_STAGE = "stage";
				
		private static final String SESSION_COUNTRY_ID = "countryid";
		
		private static final int COUNTRY_STAGE = 1;
		
		private static final int ANSWER_STAGE = 2;
		
		private static final String SLOT_NAME = "Name";
		
		private static final ArrayList<Country> COUNTRY_LIST = new ArrayList<Country>();
		
		static {
		        COUNTRY_LIST.add(new Country("THE UNITED STATES OF AMERICA", "Washington D.C."));
		 }
	
		@Override
	    public void onSessionStarted(final SessionStartedRequest request, final Session session)
	            throws SpeechletException {
	        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());
	
	        // any initialization logic goes here
	    }
	
	 	@Override
	    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
	            throws SpeechletException {
	        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	        return handleAskMeACountryIntent(session);
	    }
	 	
	 	@Override
	    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
	            throws SpeechletException {
	        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	        Intent intent = request.getIntent();
	        String intentName = (intent != null) ? intent.getName() : null;

	        if ("AskMeACountryIntent".equals(intentName)) {
	            return handleAskMeACountryIntent(session);
	        } else if ("AnswerCapitalIntent".equals(intentName)) {
	            return handleAnswerCapitalIntent(intent, session);
	        } else if ("AMAZON.HelpIntent".equals(intentName)) {
	            String speechOutput = "";
	            int stage = -1;
	            if (session.getAttributes().containsKey(SESSION_STAGE)) {
	                stage = (Integer) session.getAttribute(SESSION_STAGE);
	            }
	            switch (stage) {
	                case 0:
	                    speechOutput =
	                            "Country Capitals is a good way to learn capitals of countries."
	                            		+ "You can start by saying ask me country capitals, or you can say exit.";
	                    break;
	                case 1:
	                    speechOutput = "You can say the answer, or you can exit.";
	                    break;
	                case 2:
	                    speechOutput = "You can say, I do not know, or you can say exit.";
	                    break;
	                default:
	                    speechOutput =
	                    		"Country Capitals is a good way to learn capitals of countries."
	                    				+ "You can start by saying ask me country capitals, or you can say exit.";
	            }

	            String repromptText = speechOutput;
	            return newAskResponse(speechOutput, false, repromptText, false);
	        } else if ("AMAZON.StopIntent".equals(intentName)) {
	            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
	            outputSpeech.setText("Goodbye");

	            return SpeechletResponse.newTellResponse(outputSpeech);
	        } else if ("AMAZON.CancelIntent".equals(intentName)) {
	            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
	            outputSpeech.setText("Goodbye");

	            return SpeechletResponse.newTellResponse(outputSpeech);
	        } else {
	            throw new SpeechletException("Invalid Intent");
	        }
	    }
	 	
	    @Override
	    public void onSessionEnded(final SessionEndedRequest request, final Session session)
	            throws SpeechletException {
	        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	        // any session cleanup logic would go here
	    }
	    
	    
	    private SpeechletResponse handleAskMeACountryIntent(final Session session) {
	        String speechOutput = "";

	        // Reprompt speech will be triggered if the user doesn't respond.
	        String repromptText = "You can ask, give me a country.";

	        // / Select a random country and store it in the session variables
	        int countryId = (int) Math.floor(Math.random() * COUNTRY_LIST.size());

	        // The stage variable tracks the phase of the dialogue.
	        // When this function completes, it will be on stage 1.
	        session.setAttribute(SESSION_STAGE, COUNTRY_STAGE);
	        session.setAttribute(SESSION_COUNTRY_ID, countryId);
	        speechOutput = "What is the captial of " + COUNTRY_LIST.get(countryId).name;

	        // Create the Simple card content.
	        SimpleCard card = new SimpleCard();
	        card.setTitle("Country Capitals");
	        card.setContent(speechOutput);

	        SpeechletResponse response = newAskResponse(speechOutput, false,
	                repromptText, false);
	        response.setCard(card);
	        return response;
	    }
	    
	    private SpeechletResponse handleAnswerCapitalIntent(final Intent intent, final Session session){
	    	 String speechOutput = "", repromptText = "";

	    	 if (session.getAttributes().containsKey(SESSION_STAGE)) {
	             if ((Integer) session.getAttribute(SESSION_STAGE) == ANSWER_STAGE) {
	                 // Retrieve the joke's setup text.
	                 int countryId = (Integer) session.getAttribute(SESSION_COUNTRY_ID);
	                 String answer = intent.getSlot(SLOT_NAME).getValue();
	                 if (answer.equals(COUNTRY_LIST.get(countryId).name))
	                 	speechOutput = "That is correct " + COUNTRY_LIST.get(countryId).capital  + " is the capital of " +  COUNTRY_LIST.get(countryId).name;
	                 else
	                	 speechOutput = "You got it wrong " + COUNTRY_LIST.get(countryId).capital  + " is the capital of " +  COUNTRY_LIST.get(countryId).name + "Better luck next time";
	                 repromptText = "Can you tell me the captial of " + COUNTRY_LIST.get(countryId).name;
	             } 
	         } else {
	             // If the session attributes are not found, the question must restart.
	             speechOutput =
	                     "Sorry, I couldn't correctly retrieve the country. You can say, tell me a country.";
	             repromptText = "You can say, tell me a country.";
	         }

	         return newAskResponse("<speak>" + speechOutput + "</speak>", true, repromptText, false);
	    	
	    }
	    private SpeechletResponse newAskResponse(String stringOutput, boolean isOutputSsml,
	            String repromptText, boolean isRepromptSsml) {
	        OutputSpeech outputSpeech, repromptOutputSpeech;
	        if (isOutputSsml) {
	            outputSpeech = new SsmlOutputSpeech();
	            ((SsmlOutputSpeech) outputSpeech).setSsml(stringOutput);
	        } else {
	            outputSpeech = new PlainTextOutputSpeech();
	            ((PlainTextOutputSpeech) outputSpeech).setText(stringOutput);
	        }

	        if (isRepromptSsml) {
	            repromptOutputSpeech = new SsmlOutputSpeech();
	            ((SsmlOutputSpeech) repromptOutputSpeech).setSsml(repromptText);
	        } else {
	            repromptOutputSpeech = new PlainTextOutputSpeech();
	            ((PlainTextOutputSpeech) repromptOutputSpeech).setText(repromptText);
	        }
	        Reprompt reprompt = new Reprompt();
	        reprompt.setOutputSpeech(repromptOutputSpeech);
	        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	    }
	    
	    private static class Country {

	        private final String name;
	        private final String capital;

	        Country(String name, String capital) {
	            this.name = name;
	            this.capital = capital;
	        }
	    }

}
