import openai
from dotenv import load_dotenv
import os

load_dotenv()  # Loads variables from .env

api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    raise ValueError("OPENAI_API_KEY is not set in .env file")

client = openai.OpenAI(api_key=api_key)

response = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "capital of France? also tell me what gpt version are you in one sentence and what is your knowledge cutoff date?"}],
)

print(response.choices[0].message.content)
