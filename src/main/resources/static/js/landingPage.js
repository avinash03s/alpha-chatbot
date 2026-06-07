//const demoMessages = [
//  { role: 'ai',   text: 'Hi! I\'m Alpha, your AI assistant. How can I help you today?' },
//  { role: 'user', text: 'Write a professional email to my client about a project delay.' },
//  { role: 'ai',   text: 'Sure! Here\'s a professional email:\n\n<em>Subject: Project Timeline Update</em>\n\nDear [Client Name],\nI wanted to proactively update you regarding our project timeline. Due to unforeseen circumstances, we need a short extension...' },
//];
//
//const usecaseTags = [
//  '📧 Email writing',
//  '📚 Learning & education',
//  '💼 Business reports',
//  '🎯 Goal planning',
//  '🧠 Concept explanation',
//  '💻 Coding help',
//  '🌍 Translation',
//  '📝 Summarization',
//  '🎨 Creative writing',
//  '🔍 Fact checking',
//  '📊 Data analysis',
//  '🗣️ Interview prep',
//];
//
//function renderTags() {
//  const container = document.getElementById('usecaseTags');
//  if (!container) return;
//  usecaseTags.forEach(tag => {
//    const span = document.createElement('span');
//    span.className = 'uc-tag';
//    span.textContent = tag;
//    container.appendChild(span);
//  });
//}
//
//function renderChat() {
//  const body = document.getElementById('chatDemo');
//  if (!body) return;
//
//  let i = 0;
//
//  function showNext() {
//    if (i >= demoMessages.length) {
//      const typing = document.createElement('div');
//      typing.className = 'chat-msg';
//      typing.innerHTML = `
//        <div class="chat-avatar ai">α</div>
//        <div class="typing-dots">
//          <span></span><span></span><span></span>
//        </div>`;
//      body.appendChild(typing);
//      return;
//    }
//
//    const msg = demoMessages[i];
//    const div = document.createElement('div');
//    div.className = `chat-msg ${msg.role === 'user' ? 'user' : ''}`;
//    div.innerHTML = `
//      <div class="chat-avatar ${msg.role === 'user' ? 'user-av' : 'ai'}">
//        ${msg.role === 'user' ? 'U' : 'α'}
//      </div>
//      <div class="chat-bubble">${msg.text.replace(/\n/g, '<br>')}</div>`;
//    body.appendChild(div);
//    i++;
//    setTimeout(showNext, 900);
//  }
//
//  showNext();
//}
//
//function initNavbar() {
//  const nav = document.querySelector('.alpha-navbar');
//  if (!nav) return;
//  window.addEventListener('scroll', () => {
//    nav.style.boxShadow = window.scrollY > 10
//      ? '0 4px 24px rgba(0,0,0,0.4)'
//      : 'none';
//  });
//}
//
//function initScrollReveal() {
//  const els = document.querySelectorAll('.feat-card, .step-card, .stat-num');
//  if (!('IntersectionObserver' in window)) return;
//
//  const observer = new IntersectionObserver((entries) => {
//    entries.forEach(e => {
//      if (e.isIntersecting) {
//        e.target.style.opacity = '1';
//        e.target.style.transform = 'translateY(0)';
//        observer.unobserve(e.target);
//      }
//    });
//  }, { threshold: 0.15 });
//
//  els.forEach(el => {
//    el.style.opacity = '0';
//    el.style.transform = 'translateY(20px)';
//    el.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
//    observer.observe(el);
//  });
//}
//
//document.addEventListener('DOMContentLoaded', () => {
//  renderTags();
//  renderChat();
//  initNavbar();
//  initScrollReveal();
//});