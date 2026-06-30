importScripts('./ngsw-worker.js');

self.addEventListener('push', (event) => {
  const data = event.data ? event.data.text() : 'Erinnerung fällig';

  const options = {
    body: data,
    icon: '/icons/icon-192x192.png',
    badge: '/icons/icon-72x72.png'
  };

  event.waitUntil(
    self.registration.showNotification('Plant Care', options)
  );
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  event.waitUntil(
    clients.openWindow('/')
  );
});
