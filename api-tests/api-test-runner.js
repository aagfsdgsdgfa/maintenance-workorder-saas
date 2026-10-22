#!/usr/bin/env node
/**
 * API Test Runner for maintenance (智维云服)
 * Tests all mini-program endpoints with authentication.
 *
 * Usage: node api-test-runner.js
 * Requires: mysql2 (already available at /tmp/node_modules/mysql2)
 */

const http = require('http');
const mysql = require('mysql2');

const CONFIG = {
  backendHost: 'localhost',
  backendPort: 8080,
  mysql: {
    host: 'localhost',
    port: 3306,
    user: 'maintenance',
    password: '77zTJfkCYkfxpDzt',
    database: 'maintenance'
  }
};

// Engineer accounts with zw_engineer role
const ENGINEER_ACCOUNTS = [
  { userId: 1,  username: 'admin',    password: '123456' },
  { userId: 2,  username: 'ry',      password: '123456' },
  { userId: 100, username: 'zbh',    password: '123456' },
  { userId: 204, username: 'yzz',   password: '123456' },
  { userId: 206, username: 'zbh3',   password: '123456' },
];

// ─── HTTP helper ──────────────────────────────────────────────────────────────

function httpRequest(method, path, token, body) {
  return new Promise((resolve, reject) => {
    const headers = {
      'Host': CONFIG.backendHost,
      'Content-Type': 'application/json'
    };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const options = {
      hostname: CONFIG.backendHost,
      port: CONFIG.backendPort,
      path,
      method,
      headers
    };

    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', c => data += c);
      res.on('end', () => {
        try { resolve(JSON.parse(data)); }
        catch (e) { resolve({ _raw: data.substring(0, 300) }); }
      });
    });
    req.on('error', e => reject(e.message));
    req.setTimeout(8000, () => { req.destroy(); reject('timeout'); });
    if (body) req.write(typeof body === 'string' ? body : JSON.stringify(body));
    req.end();
  });
}

// ─── MySQL helper ─────────────────────────────────────────────────────────────

function mysqlQuery(sql) {
  return new Promise((resolve, reject) => {
    const conn = mysql.createConnection({ ...CONFIG.mysql, connectTimeout: 5000 });
    conn.query(sql, (err, rows) => {
      if (err) reject(err.message); else resolve(rows);
    });
    conn.on('error', e => reject(e.message));
  });
}

// ─── Captcha helpers ──────────────────────────────────────────────────────────

async function getCaptcha() {
  const data = await httpRequest('GET', '/captchaImage', null, null);
  return data;
}

async function disableCaptcha() {
  await mysqlQuery('UPDATE sys_config SET config_value = "false" WHERE config_key = "sys.account.captchaEnabled"');
}

async function enableCaptcha() {
  await mysqlQuery('UPDATE sys_config SET config_value = "true" WHERE config_key = "sys.account.captchaEnabled"');
}

// ─── Auth helpers ─────────────────────────────────────────────────────────────

async function login(username, password) {
  const result = await httpRequest('POST', '/zw/login', null, {
    username, password
  });
  if (result.code === 200 && result.token) {
    return result.token;
  }
  throw new Error(`Login failed for ${username}: ${result.msg || JSON.stringify(result)}`);
}

async function getLoginInfo(token) {
  return httpRequest('GET', '/zw/login/info', token, null);
}

// ─── API test cases ───────────────────────────────────────────────────────────

const API_TESTS = [
  {
    name: 'GET /zw/workOrder/myTasks',
    method: 'GET',
    path: '/zw/workOrder/myTasks',
    requiresAuth: true,
    description: 'Get my assigned work orders'
  },
  {
    name: 'GET /zw/inspection/myTasks',
    method: 'GET',
    path: '/zw/inspection/myTasks',
    requiresAuth: true,
    description: 'Get my assigned inspection tasks'
  },
  {
    name: 'GET /zw/dailyReport/list',
    method: 'GET',
    path: '/zw/dailyReport/list',
    requiresAuth: true,
    description: 'Get daily reports list'
  },
  {
    name: 'GET /zw/checkin/list',
    method: 'GET',
    path: '/zw/checkin/list',
    requiresAuth: true,
    description: 'Get check-in records'
  },
  {
    name: 'GET /zw/fault/list',
    method: 'GET',
    path: '/zw/fault/list',
    requiresAuth: true,
    description: 'Get fault records'
  },
  {
    name: 'GET /zw/knowledgeBase/search',
    method: 'GET',
    path: '/zw/knowledgeBase/search',
    requiresAuth: true,
    description: 'Search knowledge base'
  },
  {
    name: 'GET /zw/notification/list',
    method: 'GET',
    path: '/zw/notification/list',
    requiresAuth: true,
    description: 'Get notification list'
  },
  {
    name: 'GET /zw/workbench/summary',
    method: 'GET',
    path: '/zw/workbench/summary',
    requiresAuth: true,
    description: 'Get workbench summary'
  },
  {
    name: 'GET /zw/dashboard/summary',
    method: 'GET',
    path: '/zw/dashboard/summary',
    requiresAuth: true,
    description: 'Get dashboard summary'
  },
  {
    name: 'GET /zw/asset/list',
    method: 'GET',
    path: '/zw/asset/list',
    requiresAuth: true,
    description: 'Get asset list'
  },
];

// ─── Main test runner ─────────────────────────────────────────────────────────

async function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function waitForBackend(maxAttempts = 30) {
  console.log('Waiting for backend to come online...');
  for (let i = 1; i <= maxAttempts; i++) {
    try {
      const data = await httpRequest('GET', '/captchaImage', null, null);
      if (data.code !== undefined) {
        console.log(`Backend is online! (captchaEnabled=${data.captchaEnabled})`);
        return data;
      }
    } catch (e) {
      process.stdout.write('.');
    }
    await sleep(1000);
  }
  throw new Error('Backend did not come online within ' + maxAttempts + ' seconds');
}

function formatResult(testName, result) {
  const code = result.code;
  const emoji = code === 200 ? '[OK]' : code === 401 ? '[AUTH]' : '[FAIL]';
  const summary = result.rows ? `rows=${result.rows.length}, total=${result.total}` :
                  result.data ? JSON.stringify(result.data).substring(0, 80) :
                  result.msg || '';
  return `  ${emoji} ${testName}: code=${code} ${summary}`;
}

async function runTests() {
  console.log('=== maintenance API Test Suite ===\n');

  // Step 1: Wait for backend
  try {
    await waitForBackend(30);
  } catch (e) {
    console.error('ERROR: ' + e.message);
    console.error('The backend is not responding. Please start the backend server and try again.');
    process.exit(1);
  }

  // Step 2: Disable captcha
  console.log('\n[1] Disabling captcha via MySQL...');
  try {
    await disableCaptcha();
    console.log('    Captcha disabled (sys.account.captchaEnabled = false)');
  } catch (e) {
    console.error('    WARNING: Could not disable captcha: ' + e);
  }

  // Small delay for cache to expire
  await sleep(2000);

  // Step 3: Try each engineer account
  let token = null;
  let loginInfo = null;
  let usedAccount = null;

  for (const account of ENGINEER_ACCOUNTS) {
    try {
      console.log(`\n[2] Trying login with account: ${account.username}...`);
      token = await login(account.username, account.password);
      usedAccount = account;
      loginInfo = await getLoginInfo(token);
      console.log(`    SUCCESS! Logged in as ${account.username}`);
      console.log(`    userId=${loginInfo.userId}, nickName=${loginInfo.nickName}`);
      console.log(`    token=${token.substring(0, 40)}...`);
      break;
    } catch (e) {
      console.log(`    FAILED: ${e.message}`);
    }
  }

  if (!token) {
    console.error('\nERROR: Could not login with any engineer account.');
    console.error('Tried accounts:', ENGINEER_ACCOUNTS.map(a => a.username).join(', '));
    console.error('Password: 123456 (initPassword from sys_config)');
    console.error('All accounts require zw_engineer role.');
    process.exit(1);
  }

  // Step 4: Re-enable captcha
  console.log('\n[3] Re-enabling captcha...');
  try {
    await enableCaptcha();
    console.log('    Captcha re-enabled');
  } catch (e) {
    console.log('    WARNING: Could not re-enable captcha: ' + e);
  }

  // Step 5: Run API tests
  console.log('\n[4] Testing API endpoints...\n');
  console.log('Results:');
  console.log('--------');

  let passed = 0, failed = 0, authErrors = 0;

  for (const test of API_TESTS) {
    try {
      const result = await httpRequest(test.method, test.path, token, null);
      const code = result.code;

      if (code === 200) {
        passed++;
        console.log(formatResult(test.name, result));
      } else if (code === 401) {
        authErrors++;
        console.log(`  [AUTH] ${test.name}: 401 - auth required or token expired`);
      } else {
        failed++;
        console.log(`  [FAIL] ${test.name}: code=${code} msg=${result.msg || 'unknown error'}`);
      }
    } catch (e) {
      failed++;
      console.log(`  [ERR]  ${test.name}: ${e}`);
    }
  }

  // Step 6: Summary
  console.log('\n========== SUMMARY ==========');
  console.log(`Total tests: ${API_TESTS.length}`);
  console.log(`Passed:      ${passed}`);
  console.log(`Auth errors: ${authErrors}`);
  console.log(`Failed:      ${failed}`);
  console.log(`\nToken used:  ${usedAccount.username} (userId=${usedAccount.userId})`);

  if (failed === 0 && authErrors === 0) {
    console.log('\nAll APIs are working correctly!');
  } else {
    console.log('\nSome issues were detected. Review the results above.');
  }
}

// ─── Run ───────────────────────────────────────────────────────────────────────

runTests().catch(e => {
  console.error('Fatal error:', e.message);
  process.exit(1);
});