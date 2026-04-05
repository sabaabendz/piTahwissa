// ========== PARTICLES ==========
function createParticles() {
  const container = document.getElementById('particles');
  if (!container) return;
  for (let i = 0; i < 25; i++) {
    const p = document.createElement('div');
    p.className = 'particle';
    p.style.left = Math.random() * 100 + '%';
    p.style.width = p.style.height = (Math.random() * 6 + 3) + 'px';
    p.style.animationDuration = (Math.random() * 15 + 10) + 's';
    p.style.animationDelay = (Math.random() * 10) + 's';
    p.style.opacity = Math.random() * 0.5 + 0.2;
    container.appendChild(p);
  }
}

// ========== NAVBAR SCROLL ==========
function initNavbar() {
  const navbar = document.getElementById('navbar');
  if (!navbar) return;
  window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 50);
  });
}

// ========== SCROLL ANIMATIONS ==========
function initScrollAnimations() {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.animationPlayState = 'running';
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.animate-fade-up, .animate-fade-left, .animate-fade-right, .animate-scale').forEach(el => {
    el.style.animationPlayState = 'paused';
    observer.observe(el);
  });
}

// ========== COUNTER ANIMATION ==========
function initCounters() {
  const counters = document.querySelectorAll('.counter');
  if (!counters.length) return;
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const el = entry.target;
        const target = parseInt(el.dataset.target);
        let current = 0;
        const step = Math.ceil(target / 60);
        const timer = setInterval(() => {
          current += step;
          if (current >= target) { current = target; clearInterval(timer); }
          el.textContent = current.toLocaleString();
        }, 30);
        observer.unobserve(el);
      }
    });
  }, { threshold: 0.5 });
  counters.forEach(c => observer.observe(c));
}

// ========== SMOOTH SCROLL ==========
document.querySelectorAll('a[href^="#"]').forEach(a => {
  a.addEventListener('click', function(e) {
    const id = this.getAttribute('href');
    if (id === '#') return;
    e.preventDefault();
    const target = document.querySelector(id);
    if (target) target.scrollIntoView({ behavior: 'smooth', block: 'start' });
  });
});

// ========== DESTINATION CARDS HOVER ==========
function initCardEffects() {
  document.querySelectorAll('.destination-card, .feature-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
      this.style.transform = 'translateY(-8px) scale(1.02)';
      this.style.boxShadow = '0 20px 50px rgba(0,0,0,0.2)';
    });
    card.addEventListener('mouseleave', function() {
      this.style.transform = '';
      this.style.boxShadow = '';
    });
  });
}

// ========== DASHBOARD: SIDEBAR TOGGLE ==========
function toggleSidebar() {
  const sidebar = document.querySelector('.sidebar');
  if (sidebar) sidebar.classList.toggle('open');
}

// ========== DASHBOARD: CHART PLACEHOLDER ==========
function initMiniCharts() {
  document.querySelectorAll('.mini-chart').forEach(canvas => {
    const ctx = canvas.getContext('2d');
    const w = canvas.width = canvas.offsetWidth;
    const h = canvas.height = canvas.offsetHeight;
    const points = Array.from({ length: 12 }, () => Math.random() * h * 0.6 + h * 0.2);
    const color = canvas.dataset.color || '#1565C0';

    ctx.beginPath();
    ctx.moveTo(0, h);
    points.forEach((y, i) => {
      const x = (i / (points.length - 1)) * w;
      if (i === 0) ctx.moveTo(x, y);
      else {
        const px = ((i - 1) / (points.length - 1)) * w;
        const cpx = (px + x) / 2;
        ctx.bezierCurveTo(cpx, points[i - 1], cpx, y, x, y);
      }
    });
    ctx.lineTo(w, h);
    ctx.lineTo(0, h);
    ctx.closePath();
    const grad = ctx.createLinearGradient(0, 0, 0, h);
    grad.addColorStop(0, color + '40');
    grad.addColorStop(1, color + '05');
    ctx.fillStyle = grad;
    ctx.fill();

    ctx.beginPath();
    points.forEach((y, i) => {
      const x = (i / (points.length - 1)) * w;
      if (i === 0) ctx.moveTo(x, y);
      else {
        const px = ((i - 1) / (points.length - 1)) * w;
        const cpx = (px + x) / 2;
        ctx.bezierCurveTo(cpx, points[i - 1], cpx, y, x, y);
      }
    });
    ctx.strokeStyle = color;
    ctx.lineWidth = 2.5;
    ctx.stroke();
  });
}

// ========== NOTIFICATION DROPDOWN ==========
function initNotifications() {
  document.querySelectorAll('.notification-btn').forEach(btn => {
    btn.addEventListener('click', function(e) {
      e.stopPropagation();
      const dropdown = this.nextElementSibling;
      if (dropdown && dropdown.classList.contains('notif-dropdown')) {
        dropdown.classList.toggle('show');
      }
    });
  });
  document.addEventListener('click', () => {
    document.querySelectorAll('.notif-dropdown.show').forEach(d => d.classList.remove('show'));
  });
}

// ========== INIT ==========
document.addEventListener('DOMContentLoaded', () => {
  createParticles();
  initNavbar();
  initScrollAnimations();
  initCounters();
  initCardEffects();
  initMiniCharts();
  initNotifications();
});
