import openai
from dotenv import load_dotenv
import os

load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")

if not api_key:
    raise ValueError("OPENAI_API_KEY is not set in .env file")

client = openai.OpenAI(api_key=api_key)

# Read the input file content
input_file = "03_in.txt"
try:
    with open(input_file, 'r', encoding='utf-8') as file:
        input_content = file.read().strip()
    print(f"📄 Read {len(input_content)} characters from {input_file}")
except FileNotFoundError:
    print(f"❌ Error: {input_file} not found")
    exit(1)
except Exception as e:
    print(f"❌ Error reading {input_file}: {e}")
    exit(1)

try:
    response = client.responses.create(
        model="gpt-5",
        input=input_content
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
