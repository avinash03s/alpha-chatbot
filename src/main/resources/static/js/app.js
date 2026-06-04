const API_URL = "/api/ai/ask";

const questionInput = document.getElementById("question");
const sendBtn = document.getElementById("sendBtn");
const chatBody = document.getElementById("chatBody");
const themeBtn = document.getElementById("themeBtn");

/* ---------- Theme ---------- */

if (themeBtn) {

    const savedTheme = localStorage.getItem("theme");

    if (savedTheme === "light") {
        document.body.classList.add("light-theme");
        themeBtn.innerHTML = '<i class="ti ti-sun"></i>';
    }

    themeBtn.addEventListener("click", () => {

        document.body.classList.toggle("light-theme");

        const isLight =
            document.body.classList.contains("light-theme");

        localStorage.setItem(
            "theme",
            isLight ? "light" : "dark"
        );

        themeBtn.innerHTML = isLight
            ? '<i class="ti ti-sun"></i>'
            : '<i class="ti ti-moon"></i>';
    });
}

/* ---------- Hero ---------- */

function hideHero() {

    const hero =
        document.querySelector(".hero");

    if (hero) {
        hero.remove();
    }
}

/* ---------- Quick Ask ---------- */

function quickAsk(text) {

    questionInput.value = text;

    askAI();
}

/* ---------- Messages ---------- */

function addMessage(text, type) {

    hideHero();

    const wrapper =
        document.createElement("div");

    wrapper.className =
        `message ${type}`;

    wrapper.innerHTML = `
        <div class="bubble">
            ${text}
        </div>
    `;

    chatBody.appendChild(wrapper);

    chatBody.scrollTop =
        chatBody.scrollHeight;
}

/* ---------- Typing ---------- */

function showTyping() {

    const typing =
        document.createElement("div");

    typing.className = "message bot";
    typing.id = "typing";

    typing.innerHTML = `
        <div class="bubble">
            Alpha is thinking...
        </div>
    `;

    chatBody.appendChild(typing);

    chatBody.scrollTop =
        chatBody.scrollHeight;
}

function removeTyping() {

    const typing =
        document.getElementById("typing");

    if (typing) {
        typing.remove();
    }
}

/* ---------- Ask AI ---------- */

async function askAI() {

    const question =
        questionInput.value.trim();

    if (!question) {
        return;
    }

    addMessage(question, "user");

    questionInput.value = "";
    questionInput.style.height = "48px";

    showTyping();

    sendBtn.disabled = true;

    try {

        const response =
            await fetch(API_URL, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    question: question
                })
            });

        removeTyping();

        if (!response.ok) {

            addMessage(
                "Something went wrong.",
                "bot"
            );

            return;
        }

        const answer =
            await response.text();

        addMessage(answer, "bot");

    } catch (error) {

        removeTyping();

        addMessage(
            "Unable to connect to Alpha.",
            "bot"
        );

        console.error(error);

    } finally {

        sendBtn.disabled = false;
    }
}

/* ---------- Send Button ---------- */

if (sendBtn) {
    sendBtn.addEventListener(
        "click",
        askAI
    );
}

/* ---------- Enter Key ---------- */

questionInput.addEventListener(
    "keydown",
    function (e) {

        if (
            e.key === "Enter" &&
            !e.shiftKey
        ) {

            e.preventDefault();

            askAI();
        }
    }
);

/* ---------- Auto Resize ---------- */

questionInput.addEventListener(
    "input",
    function () {

        this.style.height = "48px";

        this.style.height =
            this.scrollHeight + "px";
    }
);