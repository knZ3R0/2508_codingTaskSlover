import openai
from dotenv import load_dotenv
import os

load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    raise ValueError("OPENAI_API_KEY is not set in .env file")

client = openai.OpenAI(api_key=api_key)

try:
    response = client.responses.create(
        model="gpt-5",
        input="What is the capital of France? Also tell me what GPT version you are and your knowledge cutoff date—in one sentence."
    )

    found_output = False
    for item in response.output:
        if item.type == "message" and hasattr(item, "content"):
            for c in item.content:
                if c.type == "output_text":
                    print(c.text)
                    found_output = True

    if not found_output:
        print("⚠️ Response received, but no output_text found in message content.")

except openai.APIStatusError as e:
    print(f"❌ API error: {e.status_code} - {e.message}")
except Exception as ex:
    print(f"❌ Unexpected error: {type(ex).__name__}: {ex}")
